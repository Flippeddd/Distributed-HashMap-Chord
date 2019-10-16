package chord;

public class Node {
    int nid;
    Node predecessor;
    Node successor;
    FingerTable fingerTable;
    //exist or not

    public Node(int nid) {
        this.nid = nid;
        this.predecessor = null;
        this.successor = this;
        this.fingerTable = new FingerTable(this);
    }
    /**
     * Look up.
     * @param node
     * @param id
     * @return id's successor node
     */
    // node?
    public Node find_successor(int id) {
        Node prevNode = find_predecessor(id);
        return prevNode.successor;
    }
    private Node find_predecessor(int id) {
        Node temp = new Node(nid = id);
        while (!inCurInterval(temp, temp.successor, id)) {
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
    public void join(int id, Node existNode){
        // need to edit
        Node newNode = new Node(nid = id);
        if(existNode != null){
            initFingerTable(id,existNode);
            updateOthers(id);
        }else{
            // the join node is the only node in the chord
            for(int i = 0; i < 4; i++){
                newNode.fingerTable.getFinger(i).node = newNode;
            }
            newNode.predecessor = newNode;
        }
    }
    private void initFingerTable(int id, Node existNode){
        Node newNode = new Node(nid = id);
        FingerTable ft = newNode.fingerTable;
        ft.getFinger(1).node = newNode.find_successor(existNode.fingerTable.getFinger(1).start);
        newNode.predecessor = newNode.successor.predecessor;
        newNode.successor.predecessor = newNode;
        for(int i = 0; i < 4; i++){
            int start = ft.getFinger(i+1).start;
            if(start >= id && start < getId(ft.getFinger(i).node)){
                ft.getFinger(i+1).node = ft.getFinger(i).node;
            }else{
                ft.getFinger(i+1).node = existNode.find_successor(ft.getFinger(i+1).start);
            }
        }
    }
    private void updateOthers(int id){
        Node newNode = new Node(nid = id);
        for(int i = 0; i < 4; i++) {
            Node temp = newNode.find_predecessor(id - (int)Math.pow(2, i));
            updateFingerTable(getId(temp),newNode,i);
        }
    }
    private void updateFingerTable(int id, Node node,int i){
        Node newNode = new Node(nid = id);
        int tid = getId(node);
        FingerTable ft = newNode.fingerTable;
        if(tid >= id && tid < getId(ft.getFinger(i).node)){
            ft.getFinger(i).node = node;
            Node temp = newNode.predecessor;
            updateFingerTable(getId(temp),node,i);
        }
    }
    public void leave(int id){

    }
    public int lookup(int id){

        return 0;
    }


    /**
     * @TODO updateFingers, join, leave, lookup
     */

}
