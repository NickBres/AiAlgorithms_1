public class Ex1 {
    public static void main(String[] args) {
//        if (args.length != 1) {
//            System.out.println("Usage: java Ex1 <path_to_xml_file>");
//            return;
//        }

        //String xmlFilePath = args[0];
        BayesianNetwork network = BayesianNetwork.parseXML("alarm_net.xml");
        System.out.println(network);
    }
}
