package SheetPackageTest;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.*;
import com.google.api.services.sheets.v4.Sheets;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class SheetsQuickstart {
    private static final String APPLICATION_NAME = "CSC131 Computer Software Engr - SECTION 01";
    private static final java.io.File DATA_STORE_DIR = new java.io.File(System.getProperty("user.home"), ".credentials//sheets.googleapis.com-java-quickstart.json");
    private static FileDataStoreFactory DATA_STORE_FACTORY;
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static HttpTransport HTTP_TRANSPORT;
    private static final List<String> SCOPES = Arrays.asList( SheetsScopes.SPREADSHEETS );
    private static String spreadsheetId = "1HEkPX-wEowUAOSH3rAzwLOndnAMZ_WsCkxR_aonbyu8";
    static {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
    }


    public static Credential authorize() throws IOException { 	
    	String respath = "/client_secret.json";
    	InputStream in = SheetsQuickstart.class.getResourceAsStream(respath);
    	GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(DATA_STORE_FACTORY)
                .setAccessType("offline")
                .build();
        Credential credential = new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
        System.out.println("Credentials saved to " + DATA_STORE_DIR.getAbsolutePath());
        return credential;
    }

    public static Sheets getSheetsService() throws IOException {
        Credential credential = authorize();
        return new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }
    
    public static String getDate() throws IOException{
    	//today's date
    	SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");  
        Date date = new Date();  
        return formatter.format(date);
    }
    
    public static String randomKey() throws IOException{
    	Random rand = new Random();
    	int randomKey = rand.nextInt(10000-1000) + 1000;
    	return "" + randomKey;
    }
    
    public static String timeLimit() throws IOException{
    	//returns a time 15 minutes in the future
    	SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");  
    	Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 15);
        return formatter.format(calendar.getTime());
    }
    
    public static String getKey()throws IOException{
    	Sheets service = getSheetsService();
    	//targeted range for date and key
    	final String range = "G1:P";
        ValueRange response = service.spreadsheets().values().get(spreadsheetId, range).execute();
        //2d list
        List<List<Object>> element = response.getValues();
        
        String key = "";
        int col = 6;
        //if no data is found in the element from the range
        if (element == null) {
        	//make a new key and update today's date and time limit
        	key = randomKey();
        	List<Request> r = new ArrayList<>();
        	updateSheet(r, getDate(),0,0,col);
        	updateSheet(r, key,0,31,col);
        	updateSheet(r, timeLimit(),0,32,col);
        	updateSheet(r, service);
        	System.out.println("no data was there");
        }
        //some data was found in the element from the range
        else {
        	//check if the first date slot is empty
        	if (element.get(0).isEmpty()) {
        		//make a new key and update today's date and time limit
        		key = randomKey();
        		List<Request> r = new ArrayList<>();
        		updateSheet(r, getDate(),0,0,col);
            	updateSheet(r, key,0,31,col);
            	updateSheet(r, timeLimit(),0,32,col);
            	updateSheet(r, service);
            	System.out.println("first date slot empty");
        	}
        	//first date slot not empty
        	else {
        		//find out how many dates are there
        		int i = element.get(0).size();
        		
        		//check if the last slot is today's date
        		if(element.get(0).get(i-1).equals(getDate())) {
        			try {
        				key = (String)element.get(31).get(i-1);
        				System.out.println("It's the same day");
        			}
        			catch(Exception e){
        				System.out.println("key is missing, making new key and timer");
        				key = randomKey();
            			List<Request> r = new ArrayList<>();
    	        		updateSheet(r, getDate(),0,0, col+i-1);
    	        		updateSheet(r, key,0,31, col+i-1);
    	        		updateSheet(r, timeLimit(),0,32,col+i-1);
    	        		updateSheet(r, service);
        			}
        		}
        		//different date found
        		else {
        			//set today's date, time limit, and key in the next slot
        			key = randomKey();
        			List<Request> r = new ArrayList<>();
	        		updateSheet(r, getDate(),0,0, col+i);
	        		updateSheet(r, key,0,31, col+i);
	        		updateSheet(r, timeLimit(),0,32,col+i);
	        		updateSheet(r, service);
	        		System.out.println("different date, making new key");
        		}
        	}
        }
        //returns key for today
        return key;
    }
    
    public static String getTimer()throws IOException {
    	//get today's timer
    	String timer = "";
    	Sheets service = getSheetsService();
    	//targeted range for date
    	final String range = "G1:P";
        ValueRange response = service.spreadsheets().values().get(spreadsheetId, range).execute();
        //2d list
        List<List<Object>> element = response.getValues();
        
        int col = 6;
        //if no data is found in the element from the range
        if (element == null) {
        	//nothing was there
        	timer = "";
        	System.out.println("no timer was there, do getKey() first");
        }
        //some data was found in the element from the range
        else {
        	//check if the first date slot is empty
        	if (element.get(0).isEmpty()) {
        		//no timer found
        		timer = "";
            	System.out.println("first date slot empty, do getKey() first");
        	}
        	//first date slot not empty
        	else {
        		//find out how many dates are there
        		int i = element.get(0).size();
        		
        		//check if the last slot is today's date
        		if(element.get(0).get(i-1).equals(getDate())) {
        			try {
	        			timer = (String)element.get(32).get(i-1);
	        			System.out.println("getting timer");
        			}
        			catch (Exception e) {
        				System.out.println("missing timer, do getKey() first");
        			}
        		}
        		//different date found
        		else {
        			//set today's date, time limit, and key in the next slot
        			timer = "";
	        		System.out.println("date doesn't match, do getKey() first");
        		}
        	}
        }
    	
    	return timer;
    }
    
    public static void updateSheet()throws IOException{
     	//Set Range of spread sheet Ex: 
        Sheets service = getSheetsService();
    	final String range = "!A2:E";
        ValueRange response = service.spreadsheets().values().get(spreadsheetId, range).execute();
        List<List<Object>> element = response.getValues();
        
        if(element == null || element.isEmpty())
            System.out.println("No data found.");
        else{
            System.out.println("Name -- Major");
            for (List row : element) {
                // Print columns A and E, which correspond to indices 0 and 4.
                System.out.printf("%s, %s\n", row.get(0), row.get(1));
            }
        }
    }
    
    //
    public static void updateSheet(String stringValue, int sheetId, int rowIndex, int columIndex)throws IOException{
    	Sheets service = getSheetsService();
        List<Request> requests = new ArrayList<>();
        //Adding DATE to row and column
        List<CellData> values = new ArrayList<>();
        values.add(new CellData().setUserEnteredValue(new ExtendedValue().setStringValue((stringValue))));
        requests.add(new Request()
        		.setUpdateCells(new UpdateCellsRequest()
        		.setStart(new GridCoordinate().setSheetId(sheetId).setRowIndex(rowIndex).setColumnIndex(columIndex))
        		.setRows(Arrays.asList(new RowData().setValues(values)))
        		.setFields("userEnteredValue,userEnteredFormat.backgroundColor")));     
        BatchUpdateSpreadsheetRequest batchUpdateRequest = new BatchUpdateSpreadsheetRequest().setRequests(requests);
     	service.spreadsheets().batchUpdate(spreadsheetId, batchUpdateRequest).execute();
    }
    
    public static void updateSheet(List<Request> request, String stringValue, int sheetId, int rowIndex, int columIndex)throws IOException {
    	//for multiple cell updates
        List<CellData> values = new ArrayList<>();
        values.add(new CellData().setUserEnteredValue(new ExtendedValue().setStringValue((stringValue))));
        request.add(new Request()
        		.setUpdateCells(new UpdateCellsRequest()
        		.setStart(new GridCoordinate().setSheetId(sheetId).setRowIndex(rowIndex).setColumnIndex(columIndex))
        		.setRows(Arrays.asList(new RowData().setValues(values)))
        		.setFields("userEnteredValue,userEnteredFormat.backgroundColor")));
    }
    
    public static void updateSheet(List<Request> request, Sheets service)throws IOException{
    	//executes the update
    	BatchUpdateSpreadsheetRequest batchUpdateRequest = new BatchUpdateSpreadsheetRequest().setRequests(request);
     	service.spreadsheets().batchUpdate(spreadsheetId, batchUpdateRequest).execute();
    }
    
    public static boolean checkLogin(String uname, String pword)throws IOException{
    	String dbName = "";
    	String dbWord = "";
    	Sheets service = getSheetsService();
    	final String range = "F35:P";
        ValueRange response = service.spreadsheets().values().get(spreadsheetId, range).execute();
        //2d list
        List<List<Object>> element = response.getValues();
    	try {
    		dbName = (String)element.get(0).get(0);
    		dbWord = (String)element.get(0).get(1);
    	}
    	catch(Exception e) {
    		System.out.println("missing username or password, register first");
    		return false;
    	}
    	
    	if(uname.equals(dbName) && makeHash256(pword).equals(dbWord)){
    		return true;
    	}
    	else {
    		return false;
    	}
    	
    }
    
    public static void userSetup(String uname, String pword)throws IOException {
    	Sheets service = getSheetsService();
        List<Request> req = new ArrayList<>();
    	updateSheet(req, uname, 0, 34, 5);
    	updateSheet(req, makeHash256(pword), 0, 34, 6);
    	updateSheet(req, service);
    	
    }
    
    public static String makeHash256(String str) {
    	//Hash the string to bytes
    	MessageDigest digest = null;
		try {
			digest = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
    	byte[] hash = digest.digest(str.getBytes(StandardCharsets.UTF_8));
    	
    	//Byte to hex converter to get the hashed value in hexadecimal
    	StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < hash.length; i++) {
        String hex = Integer.toHexString(0xff & hash[i]);
        if(hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
    
    public static void main(String[] args) throws IOException, ParseException {
        /*
        //updating one single cell only (1 API call per line)
    	updateSheet("6/12/2018", 0, 0, 6); //(value, SheedID, Row, Column)
    	updateSheet("Yes", 0, 1, 6);
    	updateSheet("Yes\nNote:test note", 0, 2, 7);
    	updateSheet("username",0,34,5);
    	updateSheet(makeHash256("password"),0,34,6);
    	
    	//updating multiple cells at a time (1 API call total)
    	Sheets service = getSheetsService();
        List<Request> requests = new ArrayList<>();
        updateSheet(requests, "hello1", 0, 49, 2);
        updateSheet(requests, "hello2", 0, 47, 2);
        //execute the update
        updateSheet(requests, service);
        
        //makes a new key if no key and date was found
    	System.out.println(getKey());
    	System.out.println(getTimer());
    	
    	//login info stuff
    	userSetup("teacher123", "password");
    	System.out.println(checkLogin("teacher123", "password"));
        */
    }
    
}
