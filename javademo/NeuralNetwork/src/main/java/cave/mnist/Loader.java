package cave.mnist;

public interface Loader {
	MetaData open();
	MetaData getMetaData();
	void close();
	BatchData readBatch();
}
