packag chord;

public class Node {
    int nid;
    Node predecessor;

    FingerTable fingerTable;
    //exist or not

    public Node(int nid) {
        this.nid = nid;
        this.predecessor = null;
        this.fingerTable = new FingerTable(this);
    }
    /**
     * Look up.
     * @param node
     * @param id
     * @return id's successor node
     */
    // node?
//    public Node find_successor(Node node,int id) {
//        Node prevNode = find_predecessor(node,id);
//        return prevNode.successor;
//    }
//    private Node find_predecessor(Node node, int id) {
//        Node temp = node;
//        while (!inCurInterval(temp, temp.successor, id)) {
//            temp = closest_preceding_finger(temp, id);
//        }
//        return temp;
//    }
    public Node find_successor(Node node, int id) {
        Node prevNode = find_predecessor(node, id);
        return prevNode.fingerTable.getFinger(0).node;
    }
    private Node find_predecessor(Node node, int id) {
        Node temp = node;
        while (!inCurInterval(temp, temp.fingerTable.getFinger(0).node, id)) {
            temp = closest_preceding_finger(temp, id);
        }
        return temp;
    }
    private boolean inCurInterval(Node cur, Node next, int id) {
        if (next == null) {
            return true;
        }
        int curId = cur.nid;
        int nextId = next.nid;
        if (curId >= nextId) {
            //reach the end of the ring
            if (curId < id || id <= nextId) {
                return true;
            }
        } else if (curId < id && id <= nextId) {
            //find the successor
            return true;
        }
        return false;
    }
    private Node closest_preceding_finger(Node cur, int id) {
        FingerTable fingers = cur.fingerTable;
        for (int i = fingers.getNumOfFingers() - 1; i >= 0; i--) {
            Node finger = fingers.getFinger(i).node;
            if (!inCurInterval(cur, finger, id)) {
                return finger;
            }
        }
        return cur;
    }
    private int getId(Node node){
        return node.nid;
    }
    public void join(Node node, Node existNode){
        if(existNode != null){
            initFingerTable(node,existNode);
            updateOthers(node);
        }else{
            // the join node is the only node in the chord
            for(int i = 0; i < 3; i++){
                node.fingerTable.getFinger(i).node = node;
            }
            node.predecessor = node;
        }
    }
    private void initFingerTable(Node newNode, Node existNode){
        FingerTable ft = newNode.fingerTable;
        ft.getFinger(0).node = newNode.find_successor(existNode,existNode.fingerTable.getFinger(0).start);
        newNode.predecessor = ft.getFinger(0).node.predecessor;
        ft.getFinger(0).node.predecessor = newNode;
        for(int i = 0; i < 2; i++){
            int start = ft.getFinger(i+1).start;
            /**
             * need to be edited
             */
            if(judgement(start,newNode,i)){
                ft.getFinger(i+1).node = ft.getFinger(i).node;
            }else{
                ft.getFinger(i+1).node = existNode.find_successor(existNode,ft.getFinger(i+1).start);
            }
        }
    }
    private void updateOthers(Node newNode){
        for(int i = 0; i < 3; i++) {
            int id = getId(newNode) - (int)Math.pow(2,i);
            if(id < 0){
                id += 8;
            }
            Node temp = newNode.find_predecessor(newNode,getId(newNode) - (int)Math.pow(2, i));
            updateFingerTable(temp,newNode,i);
        }
    }
    private void updateFingerTable(Node newNode, Node node,int i){
//        Node newNode = new Node(nid = id);
//        int id = getId(newNode);
        int tid = getId(node);
        FingerTable ft = newNode.fingerTable;
        if(judgement(tid,newNode,i)){
            ft.getFinger(i).node = node;
            Node temp = newNode.predecessor;
            updateFingerTable(temp,node,i);
        }
    }
    private boolean judgement(int start, Node node, int i){
        int left = getId(node);
        int right = getId(node.fingerTable.getFinger(i).node);
        if(left < right){
            return (start >= left && start < right);
        }else{
            return (start >= right && start < left + 16);
        }
    }
    public void leave(Node node){

    }

    /**
     * @TODO updateFingers, join, leave, lookup
     */
}
