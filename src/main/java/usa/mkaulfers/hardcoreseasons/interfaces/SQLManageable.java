package usa.mkaulfers.hardcoreseasons.interfaces;

public interface SQLManageable {
    public String saveQuery();
    public String deleteQuery();
    public String updateQuery();
    public String loadQuery();
}
