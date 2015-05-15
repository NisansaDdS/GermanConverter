import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Main {


    ArrayList<String> yesNo=new ArrayList<String>();
    ArrayList<String> purpose=new ArrayList<String>();
    ArrayList<String> maritial=new ArrayList<String>();
    ArrayList<String> house=new ArrayList<String>();
    ArrayList<String> realValued=new ArrayList<String>();
    HashMap<String,String[]> convertions=new HashMap<String,String[]>();


    ArrayList<String> neuralStrings=new ArrayList<String>();
    boolean isNeural=true;


    public static void main(String[] args) {
        Main m=new Main();
        m.setConversions();
        m.writeFile(m.readFile("input.csv"));
    }

    public void writeFile(String text) {
        String oFileName="output.arff";
        if(isNeural){
            oFileName="n_output.arff";
        }

        try {
            File statText = new File(oFileName);
            FileOutputStream is = new FileOutputStream(statText);
            OutputStreamWriter osw = new OutputStreamWriter(is);
            BufferedWriter w = new BufferedWriter(osw);
            w.write(text);
            w.close();
        } catch (IOException e) {
            System.err.println("Problem writing to the file SpamHam.arff");
        }
    }



    private void setConversions()
    {
        convertions.put("CHK_ACCT",new String[]{ "<0", "0<=X<200", ">=200", "unknown"});
        realValued.add("DURATION");
        convertions.put("HISTORY", new String[]{"no credits taken", "all credits at this bank paid back duly", "existing credits paid back duly till now", "delay in paying off in the past", "critical account"});
        purpose.add("NEW_CAR");
        purpose.add("USED_CAR");
        purpose.add("FURNITURE");
        purpose.add("RADIO/TV");
        purpose.add("EDUCATION");
        purpose.add("RETRAINING");
        realValued.add("AMOUNT");
        convertions.put("SAV_ACCT", new String[]{"<100", "100<=X<500", "500<=X<1000", ">=1000", "unknown"});
        convertions.put("EMPLOYMENT",new String[]{ "unemployed", "<1", "1<=X<4", "4<=X<7", ">=7"});
        realValued.add("INSTALL_RATE");
        maritial.add("MALE_DIV");
        maritial.add("MALE_SINGLE");
        maritial.add("MALE_MAR");
        yesNo.add("CO-APPLICANT");
        yesNo.add("GUARANTOR");
        convertions.put("TIME_RES",new String[]{ "Dummy","<=1", "1<X<=2", "2<X<=3", ">=4"});
        yesNo.add("REAL_ESTATE");
        yesNo.add("PROP_NONE");
        realValued.add("AGE");
        yesNo.add("OTHER_INSTALL");
        house.add("RENT");
        house.add("OWN_RES");
        realValued.add("NUM_CREDITS");
        convertions.put("JOB",new String[]{ "unemployed/ unskilled  - non-resident", "unskilled - resident", "skilled employee / official", "management/ self-employed/highly qualified employee/ officer"});
        realValued.add("NUM_DEPEND");
        yesNo.add("TELEPHONE");
        yesNo.add("FOREIGN");
        yesNo.add("RESPONSE");

        neuralStrings.add("CHK_ACCT");
        neuralStrings.add("DURATION");
        neuralStrings.add("HISTORY");
        neuralStrings.add("EMPLOYMENT");
        neuralStrings.add("REAL_ESTATE");
    }

    private String getList(ArrayList<String> sl,boolean withEmpty){

        String[] s;
        int i = 0,j=0;
        if(withEmpty){
            s=new  String[sl.size()+1];
            s[0]="EMPTY";
            i++;
        }else{
            s=new  String[sl.size()];
        }
        for (; i <s.length ; i++) {
            s[i]=sl.get(j);
            j++;
        }
        return getList(s);
    }


    private String getList(String[] s){
        StringBuilder sb=new StringBuilder();
        sb.append("{");
        for (int i = 0; i < s.length; i++) {
            sb.append("'");
            sb.append(s[i]);
            sb.append("'");
            if(i!=(s.length-1)){
                sb.append(",");
            }
        }
        sb.append("}");
        return sb.toString();
    }



    private String makeTitles(String[] t){
        StringBuilder sb=new StringBuilder();
        sb.append("@relation german\n");
        sb.append(makeListAttrib("CHK_ACCT"));
        sb.append(makeRealAttrib("DURATION"));
        sb.append(makeListAttrib("HISTORY"));
        if(!isNeural) {
            sb.append(makeListAttrib("PURPOSE", purpose, true));
            sb.append(makeRealAttrib("AMOUNT"));
            sb.append(makeListAttrib("SAV_ACCT"));
        }
        sb.append(makeListAttrib("EMPLOYMENT"));
        if(!isNeural) {
            sb.append(makeRealAttrib("INSTALL_RATE"));
            sb.append(makeListAttrib("MARITIAL", maritial, true));
            sb.append(makeynAttrib("CO-APPLICANT"));
            sb.append(makeynAttrib("GUARANTOR"));
            sb.append(makeListAttrib("TIME_RES"));
        }
        sb.append(makeynAttrib("REAL_ESTATE"));
        if(!isNeural) {
            sb.append(makeynAttrib("PROP_NONE"));
            sb.append(makeRealAttrib("AGE"));
            sb.append(makeynAttrib("OTHER_INSTALL"));
            sb.append(makeListAttrib("HOUSE", house, true));
            sb.append(makeRealAttrib("NUM_CREDITS"));
            sb.append(makeListAttrib("JOB"));
            sb.append(makeRealAttrib("NUM_DEPEND"));
            sb.append(makeynAttrib("TELEPHONE"));
            sb.append(makeynAttrib("FOREIGN"));
        }
        sb.append("@attribute class { good , bad }\n");
        sb.append("@data");
        return(sb.toString());
    }

    private String makeRealAttrib(String s) {
        return("@attribute "+s+" real\n");
    }

    private String makeynAttrib(String s) {
        return("@attribute "+s+" { y , n }\n");
    }

    private String makeListAttrib(String s) {
        return("@attribute "+s+" "+getList(convertions.get(s))+"\n");
    }

    private String makeListAttrib(String s,ArrayList<String> sl,boolean withEmpty) {
        return("@attribute "+s+" "+getList(sl,withEmpty)+"\n");
    }

    public String readFile(String path) {
        StringBuilder  sb=new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            String line = br.readLine();
           // System.out.println(line);
            String[] titles=line.split(",");
            sb.append(makeTitles(titles));

            line = br.readLine();

            while (line != null) {
                //System.out.println(line);
                sb.append(System.lineSeparator());
                String[] parts=line.split(",");
                int marCount=0;
                int housCount=0;
                int purCount=0;
                String[] con=null;
                for (int i = 1; i < titles.length; i++) {
                    int num=Integer.valueOf(parts[i]);
                    if(isNeural && !neuralStrings.contains(titles[i]) && !titles[i].equals("RESPONSE")) {
                        continue;
                    }


                    if(titles[i].equals("RESPONSE")){
                        if(num==1){
                            sb.append("good");
                        }
                        else{
                            sb.append("bad");
                        }
                    }
                    else if(yesNo.contains(titles[i])){
                        if(num==1){
                            sb.append("y");
                        }
                        else{
                            sb.append("n");
                        }
                        sb.append(",");
                    }
                    else if(purpose.contains(titles[i])){
                        if(num>0){
                            sb.append("'");
                            sb.append(purpose.get(i-4));
                            sb.append("'");
                            sb.append(",");
                        }
                        else{
                            purCount++;
                            if(purCount==purpose.size()){
                                sb.append("EMPTY");
                                sb.append(",");
                            }
                        }
                    }
                    else if(maritial.contains(titles[i])){
                        if(num>0){
                            sb.append(maritial.get(i-14));
                            sb.append(",");
                        }
                        else{
                            marCount++;
                            if(marCount==maritial.size()){
                                sb.append("EMPTY");
                                sb.append(",");
                            }
                        }
                    }
                    else if(house.contains(titles[i])){
                        if(num>0){
                            sb.append(house.get(i-24));
                            sb.append(",");
                        }
                        else{
                            housCount++;
                            if(housCount==house.size()){
                                sb.append("EMPTY");
                                sb.append(",");
                            }
                        }
                    }
                    else if(realValued.contains(titles[i])){
                        sb.append(num);
                        sb.append(",");
                    }
                    else{
                        con=convertions.get(titles[i]);
                        if(con!=null){
                            sb.append("'");
                            sb.append(con[num]);
                            sb.append("'");
                            sb.append(",");
                        }

                    }

                }
                line = br.readLine();
            }
            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
}
