import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

public class EnronEmailParser {
    /**
     * A class to parse Enron email data and perform various operations on it.
     */
    public static int counter = 0;
    private static final String EMAIL_REGEX = "^[A-Za-z.]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);
    public static ArrayList<String> from_email_address = new ArrayList<>();
    public static ArrayList<String> from_header = new ArrayList<>();
    public static ArrayList<Set<String>> to_header = new ArrayList<>();
    public static Set<String> emailAddresses = new HashSet<>();
    public static GraphAdjacencyMatrix adj_matrix;
    public static HashMap<String,Integer> received_emails = new HashMap<>();
    public static ArrayList<Set<String>> sent_emails = new ArrayList<>();

    /**
     * Recursively traverses the given directory to find all email addresses in the Enron email dataset.
     *
     * @param dir The root directory to search.
     * @return A set of all email addresses found in the dataset.
     * @throws IOException If an I/O error occurs while reading the files.
     */
    private static Set<String> getEmailAddresses(File dir) throws IOException {
        File[] files = dir.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                emailAddresses.addAll(getEmailAddresses(file));
            } else if (isValidEmail(file)) {
                counter+=1;
                ArrayList<String> emailAddress = parseEmailAddress(file);
                for (String address : emailAddress) {
                    emailAddresses.add(address);
                }
            }
        }

        return emailAddresses;
    }

    /**
     * Checks if the given file is a valid email by examining its headers.
     *
     * @param file The file to check.
     * @return true if the file is a valid email, false otherwise.
     * @throws IOException If an I/O error occurs while reading the file.
     */
    private static boolean isValidEmail(File file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line;
        boolean isHeader = false;
        while ((line = reader.readLine()) != null) {
          if (line.startsWith("From:") && !line.substring("From:".length()).trim().isEmpty()){
              isHeader = true;
          }
          else if(line.startsWith("To:") && !line.substring("To:".length()).trim().isEmpty()){
              isHeader = true;
          }
        }
        return isHeader;
    }


    /**
     * Returns a list of valid email addresses from the given email list.
     *
     * @param email_list An array of email addresses to be validated.
     * @return A list of valid email addresses.
     */
    public static ArrayList<String> validEmailAddress(String[] email_list){
        ArrayList<String> final_from = new ArrayList<>();

        for (String address : email_list) {
            address = address.trim().toLowerCase();
            address = address.replace("'","");
            if(address.startsWith(".")){
                address = address.replace(".","");
            }
            if (EMAIL_PATTERN.matcher(address).matches() && address.endsWith("@enron.com") && !address.contains("#")
                    && !address.contains("<") && !address.contains("/o") && !address.contains("..") && !address.contains("-") && !address.contains("_")) {
                final_from.add(address);
            }
        }

        return final_from;
    }

    /**
     * Parses the email addresses from a given file based on the 'To, 'From', 'CC' and 'BCC' attributes.
     *
     * @param file the file containing the email data
     * @return a list of email addresses
     * @throws IOException if an error occurs while reading the file
     */
    private static ArrayList<String> parseEmailAddress(File file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line;
        ArrayList<String> email_list = new ArrayList<String>();
        String from_email = null;
        ArrayList<String> to_emails = new ArrayList<>();
        while ((line = reader.readLine()) != null) {
            if (line.startsWith("From:")) {
                from_email = line.substring("From:".length()).trim();
                String[] from_list = from_email.split("[,]");
                if(!validEmailAddress(from_list).isEmpty()){
                    email_list.addAll(validEmailAddress(from_list));
                    from_email_address = validEmailAddress(from_list);
                }
            }
            else if (line.startsWith("To:")) {
                String to_email = line.substring("To:".length()).trim();
                String[] to_list = to_email.split("[,]");
                to_emails.addAll(validEmailAddress(to_list));
                email_list.addAll(validEmailAddress(to_list));

            }
            else if (line.startsWith("Cc :")) {
                String cc_email = line.substring("Cc :".length()).trim();
                email_list.addAll(Arrays.asList(cc_email.split("[,]")));
            }
            else if (line.startsWith("Bcc :")) {
                String bcc_email = line.substring("Bcc :".length()).trim();
                email_list.addAll(Arrays.asList(bcc_email.split("[,]")));
            }
        }
        if(!from_email_address.isEmpty()) {
            Set<String> received = new HashSet<>();
            for (String to_email : to_emails) {
                received.add(to_email.trim());
            }
            if(from_header.contains(from_email_address.get(0))){
                int indices = from_header.indexOf(from_email_address.get(0));
                Set<String> update = to_header.get(indices);
                update.addAll(received);
                to_header.set(indices,update);
            }
            else{
                from_header.add(from_email_address.get(0));
                to_header.add(received);
            }

            List<String> received_keep_check = new ArrayList<String>(received);
            for (String address : received_keep_check) {
                if (received_emails.containsKey(address)) {
                    int value = received_emails.get(address);
                    received_emails.put(address, value + 1);
                } else {
                    received_emails.put(address, 1);
                }
            }
        }
        return email_list;
    }

    /**
     * Builds the adjacency matrix representation of the email graph.
     *
     * @param size The size of the adjacency matrix.
     * @return The adjacency matrix representing the email graph.
     */
    private static int[][] buildGraph(int size){
        List<String> email_addresses_master = new ArrayList<>(emailAddresses);
        adj_matrix = new GraphAdjacencyMatrix(size);
        for(int i = 0; i<from_header.size();i++){
            for (int j = 0; j<to_header.get(i).size(); j++){
                int vert1 = email_addresses_master.indexOf(from_header.get(i));

                Set<String> sub_list = to_header.get(i);
                List<String> to_sub_list = new ArrayList<>(sub_list);
                int vert2 = email_addresses_master.indexOf(to_sub_list.get(j));
                adj_matrix.addEdge(vert1, vert2);
                adj_matrix.addEdge(vert2, vert1);
            }
        }
        return adj_matrix.adjacencyMatrix;
    }

    /**
     * Retrieves the connectors in the email graph.
     *
     * @return A list of indices representing the connectors in the graph.
     */
    public static List<Integer> getConnectors() {
        boolean[] visited = new boolean[adj_matrix.adjacencyMatrix.length];
        int[] discovery = new int[adj_matrix.adjacencyMatrix.length];
        int[] low = new int[adj_matrix.adjacencyMatrix.length];
        boolean[] isConnector = new boolean[adj_matrix.adjacencyMatrix.length];
        int time = 0;

        for (int i = 0; i < adj_matrix.adjacencyMatrix.length; i++) {
            if (!visited[i]) {
                dfs(i, visited, discovery, low, isConnector, time);
            }
        }

        List<Integer> connectors = new ArrayList<>();
        for (int i = 0; i < adj_matrix.adjacencyMatrix.length; i++) {
            if (isConnector[i]) {
                connectors.add(i);
            }
        }
        return connectors;
    }

    /**
     * Performs depth-first search (DFS) to find connectors in the graph.
     *
     * @param u           The current vertex being visited.
     * @param visited     An array indicating whether a vertex has been visited or not.
     * @param discovery   An array to store the discovery time of each vertex.
     * @param low         An array to store the low value of each vertex.
     * @param isConnector An array to mark vertices as connectors.
     * @param time        The current time.
     */
    private static void dfs(int u, boolean[] visited, int[] discovery, int[] low, boolean[] isConnector, int time) {
        visited[u] = true;
        discovery[u] = low[u] = ++time;
        int children = 0;

        for (int v = 0; v < adj_matrix.adjacencyMatrix.length; v++) {
            if (adj_matrix.adjacencyMatrix[u][v] == 1) {
                if (!visited[v]) {
                    children++;
                    dfs(v, visited, discovery, low, isConnector, time);
                    low[u] = Math.min(low[u], low[v]);

                    if ((discovery[u] == 1 && children > 1) || (discovery[u] > 1 && low[v] >= discovery[u])) {
                        isConnector[u] = true;
                    }
                } else {
                    low[u] = Math.min(low[u], discovery[v]);
                }
            }
        }
    }


    /**
     * Retrieves a list of teams containing a given vertex in the adjacency matrix.
     *
     * @param adjacencyMatrix The adjacency matrix representing email connections.
     * @param vertex          The vertex for which to find teams.
     * @return A list of integers representing the vertices in the team.
     */
    public static List<Integer> getTeams(int[][] adjacencyMatrix, int vertex) {
        boolean[] visited = new boolean[adjacencyMatrix.length];
        List<Integer> clique = new ArrayList<>();
        island_dfs(adjacencyMatrix, vertex, visited, clique);
        return clique;
    }

    /**
     * Recursive depth-first search (DFS) algorithm to find vertices in a team.
     *
     * @param adjacencyMatrix The adjacency matrix representing email connections.
     * @param vertex          The current vertex in the DFS.
     * @param visited         An array to track visited vertices.
     * @param clique          The list of vertices in the team.
     */
    private static void island_dfs(int[][] adjacencyMatrix, int vertex, boolean[] visited, List<Integer> clique) {
        visited[vertex] = true;
        clique.add(vertex);
        for (int i = 0; i < adjacencyMatrix.length; i++) {
            if (adjacencyMatrix[vertex][i] == 1 && !visited[i]) {
                island_dfs(adjacencyMatrix, i, visited, clique);
            }
        }
    }


    /**
     * Main method to parse and analyze Enron email data.
     *
     * @param args The command-line arguments. The first argument should be the path to the Enron email dataset.
     * @throws IOException if an I/O error occurs.
     */
    public static void main(String[] args) throws IOException {
        // Set the path to the Enron email dataset
        String path = args[0];
        System.out.println("Reading and processing data file...(~ 5 minutes)");
        Set<String> emailAddresses1 = getEmailAddresses(new File(path));

        buildGraph(emailAddresses.size());

        List<String> email_addresses_master = new ArrayList<>(emailAddresses);
        List<Integer> conn = getConnectors();
        System.out.println("Printing connectors, this may take up to 15 seconds.");

        if(args.length == 1){
            for (Integer integer : conn) {
                System.out.println(email_addresses_master.get(integer));
            }
        } 
        if(args.length > 1 && args[1] != null){
            BufferedWriter writer = new BufferedWriter(new FileWriter(args[1]));
            for (Integer integer : conn) {
                System.out.println(email_addresses_master.get(integer));
                writer.append(email_addresses_master.get(integer));
                writer.append("\n");
            }
            writer.close();
        }

        Scanner sc = new Scanner(System.in);
        boolean flag = true;
        while(flag){
            System.out.print("Email address of the individual (or EXIT to quit): ");
            String choice = sc.nextLine();
            choice = choice.trim().toLowerCase();
            if(choice.equals("exit")){
                flag=false;
            }
            else if(!email_addresses_master.contains(choice)){
                System.out.println("Email address (" + choice + ") not found in the dataset.");
            }
            else {
                if(!from_header.contains(choice)){
                    System.out.println("* " + choice + " has sent messages to " + 0 + " other(s)");
                }
                else{
                    int email_index_from = from_header.indexOf(choice);
                    System.out.println("* " + choice + " has sent messages to " + to_header.get(email_index_from).size()+ " other(s)");
                }

                if(!received_emails.containsKey(choice)){
                    System.out.println("* " + choice + " has received messages from " + 0 + " other(s)");
                }
                else{
                    int emails_received_count = received_emails.get(choice);
                    System.out.println("* " + choice + " has received messages from " + emails_received_count + " other(s)");
                }

                int index = email_addresses_master.indexOf(choice);
                int indi = getTeams(adj_matrix.adjacencyMatrix,index).size()-1;
                System.out.println("* " + choice + " is in a team with " + indi + " individual(s)");
            }
        }
    }
}