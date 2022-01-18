package cave.neuralnetwork.loader.image;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;

import cave.neuralnetwork.loader.BatchData;
import cave.neuralnetwork.loader.Loader;
import cave.neuralnetwork.loader.MetaData;

public class ImageLoader implements Loader {
	private String imageFileName;
	private String labelFileName;
	private int batchSize;
	
	private DataInputStream dsImages;
	private DataInputStream dsLabels;
	
	public ImageLoader(String imageFileName, String labelFileName, int batchSize) {
		this.imageFileName = imageFileName;
		this.labelFileName = labelFileName;
		this.batchSize = batchSize;
	}

	@Override
	public MetaData open() {
		
		try {
			dsImages = new DataInputStream(new FileInputStream(imageFileName));
		}
		catch(Exception e) {
			throw new LoaderException("Cannot open " + imageFileName, e);
		}
		
		try {
			dsLabels = new DataInputStream(new FileInputStream(labelFileName));
		}
		catch(Exception e) {
			throw new LoaderException("Cannot open " + labelFileName, e);
		}
		
		readMetaData();
		return null;
	}
	
	private MetaData readMetaData() {
		
		try {
			int magicLabelNumber = dsLabels.readInt();
			
			if(magicLabelNumber != 2049) {
				throw new LoaderException("Label file " + labelFileName + " has wrong format.");
			}
			
			int numberLabels = dsLabels.readInt();
			
			System.out.println("Number labels: " + numberLabels);
		} catch (IOException e) {
			throw new LoaderException("Unable to read " + labelFileName, e);
		}
		return null;
	}

	@Override
	public void close() {
		
		try {
			dsImages.close();
		}
		catch(Exception e) {
			throw new LoaderException("Cannot close " + imageFileName, e);
		}
		
		try {
			dsLabels.close();
		}
		catch(Exception e) {
			throw new LoaderException("Cannot close " + labelFileName, e);
		}
		
	}

	@Override
	public MetaData getMetaData() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BatchData readBatch() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}
