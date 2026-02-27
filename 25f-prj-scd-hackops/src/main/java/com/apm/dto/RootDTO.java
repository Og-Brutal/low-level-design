package main.java.com.apm.dto;

public class RootDTO {

    private int rootId;     // root_id (PK)
    private String root;    // root text (unique)

    // --- Constructors ---
    public RootDTO() {}

    public RootDTO(int rootId, String root) {
        this.rootId = rootId;
        this.root = root;
    }

    // --- Getters & Setters ---
    public int getRootId() {
        return rootId;
    }

    public void setRootId(int rootId) {
        this.rootId = rootId;
    }

    public String getRoot() {
        return root;
    }

    public void setRoot(String root) {
        this.root = root;
    }

    // --- toString() ---
    @Override
    public String toString() {
        return "RootDTO{" +
                "rootId=" + rootId +
                ", root='" + root + '\'' +
                '}';
    }
}
