package xmlParsers;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Map.Entry;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import utility.ElementPos;

import components.BlockShape;

/**
 * Writes one .xml file for a single BlockShape
 *
 * @author patrick.lam
 **/

public class BlockShapeXMLWriter {

	private String shapeFile;
	private String colorString;

	// Start Elements
	private StartElement blockshapeStartElement;
	private StartElement nameStartElement;
	private StartElement shapeStartElement;
	private StartElement elementStartElement;
	private StartElement rowStartElement;
	private StartElement colStartElement;
	private StartElement colorStartElement;
	private StartElement resolutionStartElement;
	private StartElement xStartElement;
	private StartElement yStartElement;

	// End Elements
	private EndElement blockshapeEndElement;
	private EndElement nameEndElement;
	private EndElement shapeEndElement;
	private EndElement elementEndElement;
	private EndElement rowEndElement;
	private EndElement colEndElement;
	private EndElement colorEndElement;
	private EndElement resolutionEndElement;
	private EndElement xEndElement;
	private EndElement yEndElement;

	// Create a XMLOutputFactory
	private XMLOutputFactory outputFactory;
	// Create XMLEventWriter
	private XMLEventWriter eventWriter;
	// Create a EventFactory
	private XMLEventFactory eventFactory;
	private XMLEvent end;
	private XMLEvent tab1;
	private XMLEvent tab2;
	private XMLEvent tab3;
	private XMLEvent name;
	private XMLEvent row;
	private XMLEvent col;
	private XMLEvent color;
	private XMLEvent x;
	private XMLEvent y;
	// Create and write Start Tag
	private StartDocument startDocument;
	

	public void setFile(String shapeFile) {
		this.shapeFile = shapeFile;
	}

	
	private void initElements() throws FileNotFoundException, XMLStreamException{
		outputFactory = XMLOutputFactory.newInstance();
		eventFactory = XMLEventFactory.newInstance();

		eventWriter = outputFactory.createXMLEventWriter(new FileOutputStream(shapeFile));

		end = eventFactory.createDTD("\n");
		tab1 = eventFactory.createDTD("	");
		tab2 = eventFactory.createDTD("		");
		tab3 = eventFactory.createDTD("			");
		startDocument = eventFactory.createStartDocument();

		blockshapeStartElement = eventFactory.createStartElement("", "", "blockshape");
		nameStartElement = eventFactory.createStartElement("", "", "name");
		shapeStartElement = eventFactory.createStartElement("", "", "shape");
		elementStartElement = eventFactory.createStartElement("", "", "element");
		rowStartElement = eventFactory.createStartElement("", "", "row");
		colStartElement = eventFactory.createStartElement("", "", "col");
		colorStartElement = eventFactory.createStartElement("", "", "color");
		resolutionStartElement = eventFactory.createStartElement("", "", "resolution");
		xStartElement = eventFactory.createStartElement("", "", "x");
		yStartElement = eventFactory.createStartElement("", "", "y");

		blockshapeEndElement = eventFactory.createEndElement("", "", "blockshape");
		nameEndElement = eventFactory.createEndElement("", "", "name");
		shapeEndElement = eventFactory.createEndElement("", "", "shape");
		elementEndElement = eventFactory.createEndElement("", "", "element");
		rowEndElement = eventFactory.createEndElement("", "", "row");
		colEndElement = eventFactory.createEndElement("", "", "col");
		colorEndElement = eventFactory.createEndElement("", "", "color");
		resolutionEndElement = eventFactory.createEndElement("", "", "resolution");
		xEndElement = eventFactory.createEndElement("", "", "x");
		yEndElement = eventFactory.createEndElement("", "", "y");

	}
	

	public void saveShapes(Entry<String, BlockShape> blockShape) throws XMLStreamException {

		try {
			initElements();
		} catch (FileNotFoundException e) {
			System.out.println("No file was found.");
		}

		// Get all necessary information of this blockShape
		name = eventFactory.createDTD(blockShape.getKey());
		x = eventFactory.createDTD((int)blockShape.getValue().getResolution().x + "");
		y = eventFactory.createDTD((int)blockShape.getValue().getResolution().y + "");

		// Start
		eventWriter.add(startDocument);
		eventWriter.add(end);

		// Blockshape start
		eventWriter.add(blockshapeStartElement);
		eventWriter.add(end);

		// Name
		eventWriter.add(tab1);
		eventWriter.add(nameStartElement);
		eventWriter.add(name);
		eventWriter.add(nameEndElement);
		eventWriter.add(end);

		// Shape Start
		eventWriter.add(tab1);
		eventWriter.add(shapeStartElement);
		eventWriter.add(end);


		// Loop through every element in the BlockShape
		for (Entry<ElementPos, Color> elements : blockShape.getValue().getShape().entrySet()) {

			row = eventFactory.createDTD(elements.getKey().row + "");
			col = eventFactory.createDTD(elements.getKey().col + "");
			colorString = elements.getValue().toString();
			color = eventFactory.createDTD(colorString.substring(colorString.lastIndexOf('[')+1, colorString.lastIndexOf(']')));

			// Element Start
			eventWriter.add(tab2);
			eventWriter.add(elementStartElement);
			eventWriter.add(end);

			// Row
			eventWriter.add(tab3);
			eventWriter.add(rowStartElement);
			eventWriter.add(row);
			eventWriter.add(rowEndElement);
			eventWriter.add(end);

			// Col
			eventWriter.add(tab3);
			eventWriter.add(colStartElement);
			eventWriter.add(col);
			eventWriter.add(colEndElement);
			eventWriter.add(end);

			// Color
			eventWriter.add(tab3);
			eventWriter.add(colorStartElement);
			eventWriter.add(color);
			eventWriter.add(colorEndElement);
			eventWriter.add(end);

			// Element End
			eventWriter.add(tab2);
			eventWriter.add(elementEndElement);
			eventWriter.add(end);

		}

		// Shape End
		eventWriter.add(tab1);
		eventWriter.add(shapeEndElement);
		eventWriter.add(end);

		// Resolution Start
		eventWriter.add(tab1);
		eventWriter.add(resolutionStartElement);
		eventWriter.add(end);

		// Resolution x
		eventWriter.add(tab2);
		eventWriter.add(xStartElement);
		eventWriter.add(x);
		eventWriter.add(xEndElement);
		eventWriter.add(end);

		// Resolution y
		eventWriter.add(tab2);
		eventWriter.add(yStartElement);
		eventWriter.add(y);
		eventWriter.add(yEndElement);
		eventWriter.add(end);

		// Resolution End
		eventWriter.add(tab1);
		eventWriter.add(resolutionEndElement);
		eventWriter.add(end);


		// BlockShape End
		eventWriter.add(blockshapeEndElement);
		eventWriter.add(end);

		// End
		eventWriter.add(eventFactory.createEndDocument());
		eventWriter.close();

	}

}