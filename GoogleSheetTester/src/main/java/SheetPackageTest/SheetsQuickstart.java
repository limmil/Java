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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    
    public static String getKey()throws IOException{
    	Sheets service = getSheetsService();
    	final String range = "G32:P";
        ValueRange response = service.spreadsheets().values().get(spreadsheetId, range).execute();
        List<List<Object>> element = response.getValues();

        if(element == null || element.isEmpty())
            System.out.println("No data found.");
        else{
            System.out.println("Key is: "+ element.get(0).get(0));
        }
    	return (String) element.get(0).get(0);	
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
    
    public static void main(String[] args) throws IOException {
        
    	updateSheet("6/12/2018", 0, 0, 6);
    	updateSheet("Yes", 0, 1, 6);
    	updateSheet("Yes\nNote:test note", 0, 2, 7);
    	updateSheet("username",0,34,5);
    	updateSheet(makeHash256("password"),0,34,6);

    }
    
}
