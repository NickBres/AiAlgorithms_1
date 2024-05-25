//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class NodeTest {
//
//    @org.junit.jupiter.api.Test
//    void createCPT() {
//        Node father = new Node("Father");
//        Node son = new Node("Son");
//        Node mother = new Node("Mother");
//
//        son.addParent(father);
//        son.addParent(mother);
//
//        father.addOutcome("One");
//        father.addOutcome("Two");
//        father.addOutcome("Three");
//
//        mother.addOutcome("T");
//        mother.addOutcome("F");
//
//        son.addOutcome("v1");
//        son.addOutcome("v2");
//        son.addOutcome("v3");
//        Map<List<String>, Double> son_res =  son.createCPT(List.of(0.11, 0.12, 0.77, 0.13, 0.14, 0.73, 0.15, 0.16, 0.69, 0.17,
//        0.18, 0.65, 0.19, 0.2, 0.61, 0.21, 0.22, 0.57));
//        System.out.println(son_res);
//
//        Map<List<String>, Double> father_res = father.createCPT(List.of(0.5, 0.25, 0.25));
//        System.out.println(father_res);
//
//        Map<List<String>, Double> mother_res = father.createCPT(List.of(0.2, 0.8));
//        System.out.println(mother_res);
//    }
//}