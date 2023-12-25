package com.demo.example.stickerapihhitter;

import android.os.AsyncTask;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class GetCategoryData {
    private static final String CategoryId = "CategoryId";
    private static final String CategoryName = "CategoryName";
    private static final String Image = "Image";

    public static void getCategoryData(final ArrayList<CategoryValues> arrayList, final AsyncTaskCmpltnNtfr asyncTaskCmpltnNtfr) {
        new AsyncTask<Void, Void, Void>() { 
            @Override 
            protected void onPreExecute() {
                arrayList.clear();
            }

            
            public Void doInBackground(Void... voidArr) {
                try {
                    Document parse = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(((HttpURLConnection) new URL("http://creinnovations.in/StickerAgain/api/xml_getallcategory.aspx").openConnection()).getInputStream());
                    parse.getDocumentElement().normalize();
                    NodeList elementsByTagName = parse.getElementsByTagName("Category");
                    for (int i = 0; i < elementsByTagName.getLength(); i++) {
                        Node item = elementsByTagName.item(i);
                        if (item.getNodeType() == 1) {
                            Element element = (Element) item;
                            arrayList.add(new CategoryValues(GetCategoryData.getValue(GetCategoryData.CategoryId, element), GetCategoryData.getValue(GetCategoryData.CategoryName, element), GetCategoryData.getValue(GetCategoryData.Image, element), ""));
                        }
                    }
                    return null;
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    return null;
                } catch (IOException e2) {
                    e2.printStackTrace();
                    return null;
                } catch (ParserConfigurationException e3) {
                    e3.printStackTrace();
                    return null;
                } catch (SAXException e4) {
                    e4.printStackTrace();
                    return null;
                } catch (Exception unused) {
                    return null;
                }
            }

            
            public void onPostExecute(Void r1) {
                asyncTaskCmpltnNtfr.onTaskCompleted();
            }
        }.execute(new Void[0]);
    }

    
    public static String getValue(String str, Element element) {
        return element.getElementsByTagName(str).item(0).getChildNodes().item(0).getNodeValue();
    }
}
