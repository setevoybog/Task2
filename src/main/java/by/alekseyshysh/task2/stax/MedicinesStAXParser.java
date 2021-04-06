package by.alekseyshysh.task2.stax;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import by.alekseyshysh.task2.entity.Dosage;
import by.alekseyshysh.task2.entity.Medicine;
import by.alekseyshysh.task2.entity.Version;
import by.alekseyshysh.task2.exception.MedicinesException;
import by.alekseyshysh.task2.tag.MedTag;
import old.CertificateBuilder;
import old.CertificateBuilderImpl;
import old.DosageBuilder;
import old.DosageBuilderImpl;
import old.MedicineBuilder;
import old.MedicineBuilderImpl;
import old.PackageBuilder;
import old.PackageBuilderImpl;
import old.VersionBuilder;
import old.VersionBuilderImpl;

public class MedicinesStAXParser {

	private XMLEventReader reader;

	private List<Medicine> medicines;
	private MedicineBuilder medicineBuilder;

	private List<String> analogs;

	private List<Version> versions;
	private VersionBuilder versionBuilder;

	private CertificateBuilder certificateBuilder;

	private PackageBuilder packageBuilder;

	private List<Dosage> dosages;
	private DosageBuilder dosageBuilder;

	public void setSettings(String path) throws MedicinesException {
		XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
		try {
			reader = xmlInputFactory.createXMLEventReader(new FileInputStream(path));
		} catch (FileNotFoundException | XMLStreamException e) {
			throw new MedicinesException(MedicinesStAXParser.class + ": file not found or xml stream exception");
		}
	}

	public List<Medicine> getMedicines() {
		return new ArrayList<>(medicines);
	}
	
	public void parse() throws MedicinesException {
		try {
			parseInternal();
		} catch (XMLStreamException e) {
			throw new MedicinesException(MedicinesStAXParser.class + ": Error while parsing");
		}
	}

	private void parseInternal() throws XMLStreamException {
		while (reader.hasNext()) {
			XMLEvent nextEvent = reader.nextEvent();
			if (nextEvent.isStartElement()) {
				StartElement startElement = nextEvent.asStartElement();
				switch (startElement.getName().getLocalPart()) {
				case MedTag.MEDICINES:
					medicines = new ArrayList<>();
					break;
				case MedTag.MEDICINE:
					medicineBuilder = new MedicineBuilderImpl();
					QName qName = new QName(startElement.getNamespaceURI(MedTag.NAMESPACE_PREFIX),
							MedTag.ATTRIBUTE_ID, MedTag.NAMESPACE_PREFIX);
					String id = startElement.getAttributeByName(qName).getValue();
					medicineBuilder.setId(id);
					break;
				case MedTag.NAME:
					nextEvent = reader.nextEvent();
					medicineBuilder.setName(nextEvent.asCharacters().getData());
					break;
				case MedTag.PHARM:
					nextEvent = reader.nextEvent();
					medicineBuilder.setPharm(nextEvent.asCharacters().getData());
					break;
				case MedTag.GROUP:
					nextEvent = reader.nextEvent();
					medicineBuilder.setGroup(nextEvent.asCharacters().getData());
					break;
				case MedTag.ANALOGS:
					analogs = new ArrayList<>();
					break;
				case MedTag.ANALOG:
//					analogBuilder = new AnalogBuilderImpl();
//					nextEvent = reader.nextEvent();
//					analogBuilder.setAnalog(nextEvent.asCharacters().getData());
//					analogs.add(analogBuilder.createInstance());
					break;
				case MedTag.VERSIONS:
					versions = new ArrayList<>();
					break;
				case MedTag.VERSION:
					versionBuilder = new VersionBuilderImpl();
					QName qDistributionVersion = new QName(startElement.getNamespaceURI(MedTag.NAMESPACE_PREFIX),
							MedTag.ATTRIBUTE_DISTRIBUTION_VERSION, MedTag.NAMESPACE_PREFIX);
					String distributionVersion = startElement.getAttributeByName(qDistributionVersion).getValue();
					versionBuilder.setDistributionVersion(distributionVersion);
					break;
				case MedTag.CERTIFICATE:
					certificateBuilder = new CertificateBuilderImpl();
					break;
				case MedTag.CERTIFICATE_NUMBER:
					nextEvent = reader.nextEvent();
					certificateBuilder.setCertificateNumber(Long.parseLong(nextEvent.asCharacters().getData()));
					break;
				case MedTag.CERTIFICATE_ISSUED_DATE_TIME:
					nextEvent = reader.nextEvent();
					String dateTimeIssued = nextEvent.asCharacters().getData();
					LocalDateTime issuedDateTime = LocalDateTime.parse(dateTimeIssued);
					certificateBuilder.setCertificateIssuedDate(issuedDateTime.toLocalDate());
					certificateBuilder.setCertificateIssuedTime(issuedDateTime.toLocalTime());
					break;
				case MedTag.CERTIFICATE_EXPIRES_DATE_TIME:
					nextEvent = reader.nextEvent();
					String dateTimeExpires = nextEvent.asCharacters().getData();
					LocalDateTime expiresDateTime = LocalDateTime.parse(dateTimeExpires);
					certificateBuilder.setCertificateExpiresDate(expiresDateTime.toLocalDate());
					certificateBuilder.setCertificateExpiresTime(expiresDateTime.toLocalTime());
					break;
				case MedTag.CERTIFICATE_REGISTERED_ORGANIZAION:
					nextEvent = reader.nextEvent();
					certificateBuilder.setCertificateRegisteredOrganization(nextEvent.asCharacters().getData());
					break;
				case MedTag.PACKAGE:
					packageBuilder = new PackageBuilderImpl();
					break;
				case MedTag.PACKAGE_TYPE:
					nextEvent = reader.nextEvent();
					packageBuilder.setPackageType(nextEvent.asCharacters().getData());
					break;
				case MedTag.PACKAGE_ELEMENTS_COUNT_IN:
					nextEvent = reader.nextEvent();
					packageBuilder.setElementsCountIn(Integer.parseInt(nextEvent.asCharacters().getData()));
					break;
				case MedTag.PACKAGE_PRICE:
					nextEvent = reader.nextEvent();
					packageBuilder.setPrice(Integer.parseInt(nextEvent.asCharacters().getData()));
					break;
				case MedTag.DOSAGES:
					dosages = new ArrayList<>();
					break;
				case MedTag.DOSAGE:
					dosageBuilder = new DosageBuilderImpl();
					break;
				case MedTag.DOSAGE_DESCRIPTION:
					nextEvent = reader.nextEvent();
					dosageBuilder.setDosageDescription(nextEvent.asCharacters().getData());
					break;
				case MedTag.DOSAGE_ACTIVE_AGENT:
					nextEvent = reader.nextEvent();
					dosageBuilder.setDosageActiveAgent(Integer.parseInt(nextEvent.asCharacters().getData()));
					break;
				case MedTag.DOSAGE_MAXIMUM_USE_PER_DAY:
					nextEvent = reader.nextEvent();
					dosageBuilder.setDosageMaximumUsePerDay(Integer.parseInt(nextEvent.asCharacters().getData()));
					break;
				default:
					// logger.log(Level.INFO, startElement.getName().getLocalPart());
					break;
				}
			}
			if (nextEvent.isEndElement()) {
				EndElement endElement = nextEvent.asEndElement();
				switch (endElement.getName().getLocalPart()) {
				case MedTag.MEDICINE:
					medicines.add(medicineBuilder.createInstance());
					break;
				case MedTag.ANALOGS:
					medicineBuilder.setAnalogs(analogs);
					break;
				case MedTag.VERSIONS:
					medicineBuilder.setVersions(versions);
					break;
				case MedTag.VERSION:
					versions.add(versionBuilder.createInstance());
					break;
				case MedTag.CERTIFICATE:
					versionBuilder.setCertificate(certificateBuilder.createInstance());
					break;
				case MedTag.PACKAGE:
					versionBuilder.setPackageEntity(packageBuilder.createInstance());
					break;
				case MedTag.DOSAGES:
					versionBuilder.setDosages(dosages);
					break;
				case MedTag.DOSAGE:
					dosages.add(dosageBuilder.createInstance());
					break;
				default:
					// logger.log(Level.INFO, endElement.getName().getLocalPart());
					break;
				}
			}
		}
	}

}
