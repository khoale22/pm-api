package com.heb.pm.util.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SequenceWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Used to aid in writing out a series of objects in a JSON format to a stream. Instantiate this class passing in
 * the ObjectMapper to use and the stream to write to. Call write() with each of the object to be written. After
 * all objects are written, call close() after all objects have been written. If close() is not called, the consumer
 * on the other end of the stream will not recognize that the stream is terminated.
 *
 * This class implements AutoCloseable, so it can be added to a try-with-resources block.
 *
 * This class is thread safe and can be used as part of the Java streaming API.
 *
 * @author d116773
 * @since 1.5.0
 * @param <T> The type to write to the stream. It must be serializable with the ObjectMapper passed in.
 */
public class StreamingOutputWriter<T> implements AutoCloseable {

	private static final Logger logger = LoggerFactory.getLogger(StreamingOutputWriter.class);

	private final transient SequenceWriter sequenceWriter;

	/**
	 * Constructs a new StreamingOutputWriter. This constructor assumes all objects will be wrapped in an array.
	 *
	 * @param objectMapper The ObjectMapper to use to convert the objects passed in to JSON.
	 * @param outputStream The OutputStream to write the objects to.
	 * @throws IOException
	 */
	public StreamingOutputWriter(ObjectMapper objectMapper, OutputStream outputStream) throws IOException {

		this(objectMapper, outputStream, true);
	}

	/**
	 * Constructs a new StreamingOutputWriter.
	 *
	 * @param objectMapper The ObjectMapper to use to convert the objects passed in to JSON.
	 * @param outputStream The OutputStream to write the objects to.
	 * @param wrapInArray Whether or not to wrap the objects being written out in an array.
	 * @throws IOException
	 */
	public StreamingOutputWriter(ObjectMapper objectMapper, OutputStream outputStream, boolean wrapInArray) throws IOException {

		Assert.notNull(outputStream, "OutputStream cannot be null.");
		Assert.notNull(objectMapper, "ObjectMapper cannot be null.");

		this.sequenceWriter = objectMapper.writer().writeValues(outputStream);
		this.sequenceWriter.init(wrapInArray);
	}

	/**
	 * Writes an object to the stream. If there is an error, a StreamingOutputException will be thrown.
	 *
	 * @param value THe object to write to the stream.
	 */
	public void write(T value) {

		synchronized (this.sequenceWriter) {
			try {
				this.sequenceWriter.write(value);
			} catch (IOException e) {
				logger.error(String.format("Error writing out '%s'.", value.toString()));
				throw new StreamingOutputException("Unable to write to stream.", e);
			}
		}
	}

	@Override
	public void close() throws IOException {

		logger.debug("Closing sequenceWriter");
		this.sequenceWriter.close();
	}
}
