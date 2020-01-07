package Player;

public class Player {
    private String FN;
    private String LN;
    private String username;
    private String password;
    private int points;
    
    public Player(){}

    public Player(String username) {
        this.username = username;
    }
    
    public Player(String fName, String lName, String Uname, String pass){
        FN = fName;
        LN = lName;
        username = Uname;
        password = pass;
        points = 0;
    }
    public Player(String fName, String lName, String Uname, String pass, int p){
        FN = fName;
        LN = lName;
        username = Uname;
        password = pass;
        points = p;
    }
    public String getFN(){
        return FN;
    }
    public String getLN(){
        return LN;
    }
    public String getUsername(){
        return username;
    }
    public String getPass(){
        return password;
    }
    public int getPoints(){
        return points;
    }
    public void setPoints(int p){
        points = p;
    }
    public void print(){
        System.out.println(FN +" "+LN+"\t"+username+" "+password+"\t"+ points);
    }
}
