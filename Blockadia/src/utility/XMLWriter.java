package utility;

import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;

import javax.security.auth.login.Configuration;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import components.BlockShape;

import framework.Config;

public class XMLWriter {

	private String shapeFile;

	public void setFile(String shapeFile) {
		this.shapeFile = shapeFile;
	}

	public void saveShapes(Config shapesList) throws Exception {
		// Create a XMLOutputFactory
		XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
		// Create XMLEventWriter
		XMLEventWriter eventWriter = outputFactory
				.createXMLEventWriter(new FileOutputStream(shapeFile));
		// Create a EventFactory
		XMLEventFactory eventFactory = XMLEventFactory.newInstance();
		XMLEvent end = eventFactory.createDTD("\n");
		XMLEvent tab = eventFactory.createDTD(" ");
		// Create and write Start Tag
		StartDocument startDocument = eventFactory.createStartDocument();
		eventWriter.add(startDocument);

		// Create Blockadia open tag
		StartElement boardStartElement = eventFactory.createStartElement("", "", "blockadia");
		eventWriter.add(end);
		eventWriter.add(boardStartElement);
		eventWriter.add(end);

		// Write the shapes tag
		StartElement shapesStartElement = eventFactory.createStartElement("", "", "shapes");
		eventWriter.add(tab);
		eventWriter.add(shapesStartElement);
		eventWriter.add(end);

		// Write the shape tag
		StartElement shapeStartElement = eventFactory.createStartElement("", "", "shape");
		EndElement shapeEndElement = eventFactory.createEndElement("", "", "shape");

		// Write name tags for shape
		StartElement nameStartElement = eventFactory.createStartElement("", "", "name");
		EndElement nameEndElement = eventFactory.createEndElement("", "", "name");

		// start populating child shapes
		for (Entry<String, BlockShape> shapes : shapesList.getGameShapesMap().entrySet()) {
			if(shapes.getKey() != Config.INITIAL_BLOCK_NAME) {
				XMLEvent name = eventFactory.createDTD(shapes.getKey());
				eventWriter.add(tab);
				eventWriter.add(tab);
				eventWriter.add(shapeStartElement);
				eventWriter.add(end);
				eventWriter.add(tab);
				eventWriter.add(tab);
				eventWriter.add(tab);
				eventWriter.add(nameStartElement);
				eventWriter.add(name);
				eventWriter.add(nameEndElement);
				eventWriter.add(end);
				eventWriter.add(tab);
				eventWriter.add(tab);
				eventWriter.add(shapeEndElement);
				eventWriter.add(end);
			}
		}


		/*for(int i = 0; i < win.getGizmos().size();i++){
eventWriter.add(tab);
createGizmoNode(eventWriter,win.getGizmos().get(i));
}*/

		EndElement shapesEndElement = eventFactory.createEndElement("", "", "shapes");
		eventWriter.add(tab);
		eventWriter.add(shapesEndElement);
		eventWriter.add(end);

		eventWriter.add(eventFactory.createEndElement("", "", "blockadia"));
		eventWriter.add(end);
		eventWriter.add(eventFactory.createEndDocument());
		eventWriter.close();
	}

/*
	private void createGizmoNode(XMLEventWriter eventWriter, gizmosInterface gizmo) throws XMLStreamException {

		XMLEventFactory eventFactory = XMLEventFactory.newInstance();
		XMLEvent end = eventFactory.createDTD("\n");
		XMLEvent tab = eventFactory.createDTD(" ");
		// Create Start node
		Attribute name = eventFactory.createAttribute("name", gizmo.getName());
		Collection<Attribute> attributeList = null;
		List nsList = Arrays.asList();
		if(gizmo.getType()=='L'||gizmo.getType()=='R'||gizmo.getType()=='T'){
			Attribute x = eventFactory.createAttribute("x",new Integer(gizmo.getX()/Configuration.L).toString());
			Attribute y = eventFactory.createAttribute("y",new Integer(gizmo.getY()/Configuration.L).toString());
			Attribute orientation = eventFactory.createAttribute("orientation",new Integer(gizmo.getOrientation()).toString());
			attributeList = Arrays.asList(name,x,y,orientation);
		}else if(gizmo.getType() == 'B'){
			double xPosition = ((ball)gizmo).getOriginalX();
			xPosition = xPosition/(Configuration.L);
			double yPosition = ((ball)gizmo).getOriginalY();
			yPosition = yPosition/(Configuration.L);
			double vx=((ball)gizmo).getOriginalVX();
			vx= vx/(Configuration.L);
			double vy=((ball)gizmo).getOriginalVY();
			vy= vy/(Configuration.L);
			Attribute x = eventFactory.createAttribute("x",new Double(xPosition).toString());
			Attribute y = eventFactory.createAttribute("y",new Double(yPosition).toString());
			Attribute xVelocity = eventFactory.createAttribute("xVelocity",new Double(vx).toString());
			Attribute yVelocity = eventFactory.createAttribute("yVelocity",new Double(vy).toString());
			attributeList = Arrays.asList(name,x,y,xVelocity,yVelocity);
		}else if(gizmo.getType() == 'A'){
			Attribute x = eventFactory.createAttribute("x",new Integer(gizmo.getX()/Configuration.L).toString());
			Attribute y = eventFactory.createAttribute("y",new Integer(gizmo.getY()/Configuration.L).toString());
			Attribute width = eventFactory.createAttribute("width",new Integer(((absorber)gizmo).getWidth()/Configuration.L).toString());
			Attribute height = eventFactory.createAttribute("height",new Integer(((absorber)gizmo).getHeight()/Configuration.L).toString());
			attributeList = Arrays.asList(name,x,y,width,height);
		}else{//squareBumper and circularBumper
			Attribute x = eventFactory.createAttribute("x",new Integer(gizmo.getX()/Configuration.L).toString());
			Attribute y = eventFactory.createAttribute("y",new Integer(gizmo.getY()/Configuration.L).toString());
			attributeList = Arrays.asList(name,x,y);
		}
		eventWriter.add(tab);
		StartElement sElement=null;
		EndElement eElement = null;
		switch(gizmo.getType()){
		case 'S':
			sElement = eventFactory.createStartElement("", "", "squareBumper",
					attributeList.iterator(), nsList.iterator());
			// Create End node
			eElement = eventFactory.createEndElement("", "", "squareBumper");
			break;
		case 'C':
			sElement = eventFactory.createStartElement("", "", "circleBumper",
					attributeList.iterator(), nsList.iterator());
			eElement = eventFactory.createEndElement("", "", "circleBumper");
			break;
		case 'T':
			sElement = eventFactory.createStartElement("", "", "triangleBumper",
					attributeList.iterator(), nsList.iterator());
			eElement = eventFactory.createEndElement("", "", "triangleBumper");
			break;
		case 'L':
			sElement = eventFactory.createStartElement("", "", "leftFlipper",
					attributeList.iterator(), nsList.iterator());
			eElement = eventFactory.createEndElement("", "", "leftFlipper");
			break;
		case 'R':
			sElement = eventFactory.createStartElement("", "", "rightFlipper",
					attributeList.iterator(), nsList.iterator());
			eElement = eventFactory.createEndElement("", "", "rightFlipper");
			break;
		case 'A':
			sElement = eventFactory.createStartElement("", "", "absorber",
					attributeList.iterator(), nsList.iterator());
			eElement = eventFactory.createEndElement("", "", "absorber");
			break;
		case 'B':
			sElement = eventFactory.createStartElement("", "", "ball",
					attributeList.iterator(), nsList.iterator());
			eElement = eventFactory.createEndElement("", "", "ball");
			break;
		default:
			break;
		}
		eventWriter.add(sElement);
		eventWriter.add(eElement);
		eventWriter.add(end); */
	}