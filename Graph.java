
import java.util.*;
import java.io.*;

class Graph3{

    static Set<Node> noder = new HashSet<>();
    static Set<Edge> edges = new HashSet<>();
    static HashMap<Node, ArrayList<Edge>> graf = new HashMap<>();
    static HashMap<String, FilmNode> filmRef = new HashMap<>();

    public static void main(String[] args){

        lesFraFil("movies.tsv", "actors.tsv");

        System.out.println("\nRaskeste vei mellom Adam Sandler og Christian Bale:\n");

        // Vilkaarlig valgte personer Adam Sandler og Christian Bale
        
        skrivUtRaskesteVei("Adam Sandler", "Christian Bale");
        System.out.println("\nBilligste vei mellom Adam Sandler og Christian Bale:\n ");
        skrivUtBilligsteVei("Adam Sandler", "Christian Bale");
        System.out.println("\n Alle komponentstorrelser og antall: \n");
        DFSFull(graf);
        
    }

    static void DFSFull(HashMap<Node, ArrayList<Edge>> graph){

        Set<Node> visited = new HashSet<>();
        HashMap<Integer, Integer> hmap = new HashMap<>();

        for(Map.Entry<Node, ArrayList<Edge>> entry : graph.entrySet()){
            int ant = DFSVisit(graph, visited, entry.getKey());
            hmap.merge(ant, 1, Integer::sum);
        }
        for(Map.Entry<Integer, Integer> entry : hmap.entrySet()){
            System.out.println("There are " + entry.getValue() + " components of size " + entry.getKey());
        }
    }

    static int DFSVisit(HashMap<Node, ArrayList<Edge>> graph, Set<Node> visited, Node s){

        Stack<Node> stack = new Stack<>();
        stack.push(s);
        int ant = 0;
        
        while(!stack.isEmpty()){
            Node u = stack.pop();
            if(!visited.contains(u)){
                visited.add(u);
                ant++;
                for(Edge edge : graph.get(s)){
                    Node nabo;
                    if(edge.til == u){
                        nabo = edge.fra;
                    }
                    else{
                        nabo = edge.til;
                    }
                    stack.push(nabo);
                }
            }
        }
        return ant;
    }

    static void skrivUtBilligsteVei(String idFra, String idTil){

        Node from = null;
        Node to = null;
        for(Node node : noder){
            if(node.navn.equals(idFra)){
                from = node;
            }
            else if(node.navn.equals(idTil)){
                to = node;
            }
        }
        List<FilmNode> listen2 = new ArrayList<>();
        List<Node> listen = Djikstra(graf, from, to);
        for(Node node : listen){
            if(node instanceof FilmNode){
                System.out.print(node);
                listen2.add((FilmNode) node);
            }
            else{
                System.out.print(node + "\n");
            }
        }
        double tot = 0;
        for(FilmNode node : listen2){
            tot += node.rating;
        }
        System.out.println("Total weight: " + ((listen2.size() * 10) - tot));
    }

    static List<Node> Djikstra(HashMap<Node, ArrayList<Edge>> graph, Node s, Node f){

        HashMap<Node, Node> path = new HashMap<>();
        HashMap<Node, Double> dist = new HashMap<>();
    
        PriorityQueue<Node> pqueue = new PriorityQueue<>(new Comparator<Node>(){
            public int compare(Node en, Node to){
                double dist1 = dist.get(en);
                double dist2 = dist.get(to);
                return Double.compare(dist2, dist1);
            }
        });

        for(Map.Entry<Node, ArrayList<Edge>> entry : graph.entrySet()){
            dist.put(entry.getKey(), Double.NEGATIVE_INFINITY);
        }
        dist.put(s, 0.0);
        pqueue.add(s);

        while(!pqueue.isEmpty()){
            Node u = pqueue.poll();
            if(u == f){
                break;
            }
            for(Edge edge : graph.get(u)){
                Node nabo;
                if(edge.fra == u){
                    nabo = edge.til;
                }
                else{
                    nabo = edge.fra;
                }
                Double c = weight(u, nabo);
                if(c > dist.get(nabo)){
                    dist.put(nabo, c);
                    pqueue.add(nabo);
                    if(path.get(u) != nabo){
                        path.put(nabo, u);
                    }
                }
            }
        }
        List<Node> aList = new ArrayList<>();
        Node u = f;
        while(u != null){
            aList.add(u);
            u = path.get(u);
            
        }
        Collections.reverse(aList);
        return aList;
    }
        
    static void skrivUtRaskesteVei(String idFra, String idTil){
        
        Node from = null;
        Node to = null;
        for(Node node : noder){
            if(node.navn.equals(idFra)){
                from = node;
            }
            if(node.navn.equals(idTil)){
                to = node;
            }
        }
        List<Node> ls = BFSRaskesteVei(graf, from, to);
        ls.remove(0);

        System.out.println(from.navn);
        for(Node node : ls){
            if(node instanceof ActorNode){
                System.out.print(node + "\n");
            } else{
                System.out.print(node);
            }
        }
    }
    
    static List<Node> BFSRaskesteVei(HashMap<Node, ArrayList<Edge>> graph, Node s, Node f){

        Map<Node, Node> pathMap = new HashMap<>();
        Set<Node> visited = new HashSet<>();
        Queue<Node> ko = new LinkedList<>();
        
        ko.add(s);
        visited.add(s);

        while(!ko.isEmpty()){
            Node u = ko.poll();
            
            if(u == f){
                break;
            }
            for(Edge edge : graf.get(u)){
                Node nabo = null;
                if(edge.fra == u){
                    nabo = edge.til;
                } else{ nabo = edge.fra;}
                
                if(!visited.contains(nabo)){
                    visited.add(nabo);
                    ko.add(nabo);
                    pathMap.put(nabo, u);
                }
            }
        }
        List<Node> shortestPath = new ArrayList<>();
        Node u = f;
        while(u != null){
            shortestPath.add(u);
            u = pathMap.get(u);
        }
        Collections.reverse(shortestPath);
        return shortestPath;
    }

    static double weight(Node u, Node v){

        ArrayList<Edge> kanterU = graf.get(u);
        if(kanterU != null){
            for(Edge edge : kanterU){
                if(edge.til.equals(v) || edge.fra.equals(v)){
                    return edge.vekt;
                }
            }
        }
        return -1;
    }
    static void lesFraFil(String filnavn, String filnavn2){

        Scanner sc;
        try{
            sc = new Scanner(new File(filnavn));
            while(sc.hasNextLine()){

                String[] linja = sc.nextLine().split("\t");
                String id = linja[0];
                String navn = linja[1];
                double rating = Double.parseDouble(linja[2]);
                FilmNode fn = new FilmNode(id, navn, rating);
                noder.add(fn);
                filmRef.put(id, fn);
                graf.put(fn, new ArrayList<>());
            }

            sc = new Scanner(new File(filnavn2));

            while(sc.hasNextLine()){

                String[] linje = sc.nextLine().split("\t");
                String id = linje[0];
                String navn = linje[1];
                ActorNode an = new ActorNode(id, navn);
                ArrayList<Edge> kanter = new ArrayList<>();
                for(int i = 2; i < linje.length; i++){
                    if(filmRef.get(linje[i]) == null){
                        continue;
                    }
                    Edge kant = new Edge(an, filmRef.get(linje[i]), filmRef.get(linje[i]).rating);
                    kanter.add(kant);
                    edges.add(kant);
                    graf.get(filmRef.get(linje[i])).add(kant);
                }
                noder.add(an);
                graf.put(an, kanter);
            }
        } catch(FileNotFoundException e){
            System.out.println(e);
        }
    }

    static class Edge{
        Node fra;
        Node til;
        double vekt;
        Edge(Node fra, Node til, double vekt){
            this.fra = fra;
            this.til = til;
            this.vekt = vekt;
        }
        @Override
        public String toString(){
            return "===[ " + fra.navn + " (" + vekt + ") ] ===> " + til.navn;
        }
    }
    static abstract class Node{
        String id;
        String navn;
        Node(String id, String navn){
            this.id = id;
            this.navn = navn;
        }
        @Override 
        public String toString(){
            return "[" + navn + "] ==>";
        }
    }
    static class FilmNode extends Node{
        double rating;
        FilmNode(String id, String navn, double rating){
            super(id, navn);
            this.rating = rating;
        }
        @Override
        public String toString(){
            return "[" + navn + " (" + rating + ")] ==>  ";
        }
    }
    static class ActorNode extends Node{
        ArrayList<String> ref;
        ActorNode(String id, String navn){
            super(id, navn);
            ref = new ArrayList<>();
        }
        @Override
        public String toString(){
            return navn;
        }
    }
}