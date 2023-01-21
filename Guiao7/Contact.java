import java.io.*;
import java.util.*;

class Contact {

    private String name;
    private int age;
    private long phoneNumber;
    private String company;     // Pode ser null
    private ArrayList<String> emails;

    public Contact (String name, int age, long phoneNumber, String company, List<String> emails) {
        this.name = name;
        this.age = age;
        this.phoneNumber = phoneNumber;
        this.company = company;
        this.emails = new ArrayList<>(emails);
    }

    public String getName() { return name; }
    public int getAge() { return age; }
    public long getPhoneNumber() { return phoneNumber; }
    public String getCompany() { return company; }
    public List<String> getEmails() { return new ArrayList(emails); }

    public String toString () {
        StringBuilder builder = new StringBuilder();
        builder.append(this.name).append(";");
        builder.append(this.age).append(";");
        builder.append(this.phoneNumber).append(";");
        builder.append(this.company).append(";");
        builder.append(this.emails.toString());
        builder.append("}");
        return builder.toString();
    }


    public void serialize (DataOutputStream out) throws IOException {

        out.writeUTF(name);
        out.writeInt(age);
        out.writeLong(phoneNumber);
        if (company==null) out.writeBoolean(false);
        else {
            out.writeBoolean(true);
            out.writeUTF(company);
        }
        out.writeInt(emails.size());
        for (String e : emails){
            out.writeUTF(e);
        }

    }


    public static Contact deserialize (DataInputStream in) throws IOException {

        String name = in.readUTF();
        int age = in.readInt();
        long phoneNumber = in.readLong();
        String company = null;
        if (in.readBoolean()) company = in.readUTF();
        List<String> emails = new ArrayList<>();
        int size = in.readInt();
        for (int i = 0; i<size; i++){
            emails.add(in.readUTF());
        }

        return new Contact(name,age,phoneNumber,company,emails);
    }







}
