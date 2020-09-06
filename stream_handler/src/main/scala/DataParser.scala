import org.apache.avro.io.DecoderFactory
import org.apache.avro.specific.SpecificDatumReader
import org.apache.avro.util.Utf8
import org.apache.flume.serialization.AvroEventDeserializer
import org.apache.flume.source.avro.AvroFlumeEvent
import org.apache.kafka.common.utils.Bytes

object DataParser {

  val reader = new SpecificDatumReader[AvroFlumeEvent](classOf[AvroFlumeEvent])

  def parse(body:Array[Byte]):(String,String) = {
    val decoder = DecoderFactory.get().binaryDecoder(body,null)
    val result = reader.read(null, decoder);
    val text = new String(result.getBody.array())
    val dataType = result.getHeaders.get(new Utf8("data_type")).toString
    (dataType,text)
  }
}
