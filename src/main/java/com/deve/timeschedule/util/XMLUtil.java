package com.deve.timeschedule.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.HashMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.xml.sax.SAXException;

//import com.aol.wap.cityguide.xmlpojo.*;

public class XMLUtil {
	private static final HashMap<String, JAXBContext> marshallContexts = new HashMap<String, JAXBContext>();

	// private static JAXBContext jaxbCtx = null;

	public static void marshall(String cntxtPkg, Object obj, OutputStream out)
			throws JAXBException, SAXException, IOException {
		Marshaller marshaller = createMarshall(cntxtPkg);
		if (marshaller == null)
			return;
		marshaller.marshal(obj, out);
	}

	public static Object unmarshall(String cntxtPkg, InputStream in)
			throws JAXBException, SAXException, IOException {
		Unmarshaller unmarshaller = createUmarshall(cntxtPkg);
		if (unmarshaller == null)
			return null;
		return unmarshaller.unmarshal(in);
		// JAXBElement<?> element = (JAXBElement<?>) unmarshaller.unmarshal(in);
		//
		// return element.getValue();
	}

	public static Object unmarshall(String cntxtPkg, File xmlFile)
			throws JAXBException, SAXException, IOException {
		return unmarshall(cntxtPkg, new BufferedInputStream(
				new FileInputStream(xmlFile)));
	}

	public static Object unmarshall(String cntxtPkg, URL url)
			throws JAXBException, SAXException, IOException {
		InputStream is = url.openStream();
		return unmarshall(cntxtPkg, new BufferedInputStream(is));
	}

	public static Object unmarshall(String cntxtPkg, String filename)
			throws JAXBException, SAXException, IOException {
		return unmarshall(cntxtPkg, new BufferedInputStream(
				new FileInputStream(filename)));
	}

	private static Unmarshaller createUmarshall(String pkgName)
			throws JAXBException, SAXException {
		JAXBContext jaxbCtx = null;
		if ((jaxbCtx = marshallContexts.get(pkgName)) == null) {
			jaxbCtx = JAXBContext.newInstance(pkgName);
			marshallContexts.put(pkgName, jaxbCtx);
		}
		Unmarshaller unmarshaller = jaxbCtx.createUnmarshaller();

		return unmarshaller;
	}

	private static Marshaller createMarshall(String pkgName)
			throws JAXBException {
		JAXBContext jaxbCtx = null;
		if ((jaxbCtx = marshallContexts.get(pkgName)) == null) {
			jaxbCtx = JAXBContext.newInstance(pkgName);
			marshallContexts.put(pkgName, jaxbCtx);
		}
		Marshaller marshaller = jaxbCtx.createMarshaller();
		return marshaller;
	}
}
