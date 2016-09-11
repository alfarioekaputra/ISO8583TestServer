/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.iso8583.client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.packager.GenericPackager;
/**
 *
 * @author rio
 */
public class Client {
    private static String messageFromServer;
    
    public static void main(String[] args) {
	Client iso = new Client();
        try {
        	String hostname = "localhost"; //hostname server iso
    		int portNumber = 5010; //port server iso
    		Socket clientSocket = new Socket(hostname,portNumber);
    		DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
                String message = iso.buildISOMessage();
                BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                if(clientSocket.isConnected()) {
                    System.out.printf("Message = %s", message+"\n");
                    outToServer.writeUTF(message + "\n");
        		outToServer.flush();
        		while ((messageFromServer = inFromServer.readLine())!= null) {
                	System.out.println("Response From Server :" + messageFromServer);
                }
            }
            outToServer.close();
            clientSocket.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public String buildISOMessage() throws Exception {
        try {
            GenericPackager packager = new GenericPackager("iso87ascii.xml");

            ISOMsg isoMsg = new ISOMsg();
            isoMsg.setPackager(packager);
            isoMsg.setMTI("0200");
            isoMsg.set(3,"370000");
            isoMsg.set(11,"003047");
            isoMsg.set(12,"083444");
            isoMsg.set(13,"0901");
            isoMsg.set(15,"0901");
            isoMsg.set(18,"6017");
            isoMsg.set(32,"111");
            isoMsg.set(41,"0011019700000000");
            isoMsg.set(42,"114209000101900");
            isoMsg.set(48,"10131608000002");
            printISOMessage(isoMsg);

            byte[] result = isoMsg.pack();
            return new String(result);
        } catch (ISOException e) {
            throw new Exception(e);
        }
    }
	
    public void printISOMessage(ISOMsg isoMsg) {
        try {
            System.out.printf("MTI = %s%n", isoMsg.getMTI());
            for (int i = 1; i <= isoMsg.getMaxField(); i++) {
                if (isoMsg.hasField(i)) {
                    System.out.printf("Field (%s) = %s%n", i, isoMsg.getString(i));
                }
            }
        } catch (ISOException e) {
            e.printStackTrace();
        }
    }
}
