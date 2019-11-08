import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;

public class App {

    static ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) {
        SparkConf conf = new SparkConf().setAppName("App").setMaster("local[*]");
        JavaSparkContext sc = new JavaSparkContext(conf);

        JavaRDD<String> rawContacts = sc.textFile("example.json");
        JavaRDD<Group> contacts = rawContacts.map(raw -> mapper.readValue(raw, Group.class));
        JavaPairRDD<String, String> membership = contacts.flatMapToPair(c -> c.getMembers().stream().map(mid -> new Tuple2<>(c.getId(), mid)).iterator());

        JavaPairRDD<String, String> it = null;
        for (int i = 0; i < 7; i++) {
            if (it == null) {
                it = membership.mapToPair(Tuple2::swap).join(membership)
                        .mapToPair(Tuple2::_2);
            }
            it = membership.union(it).mapToPair(Tuple2::swap).join(membership)
                    .mapToPair(Tuple2::_2)
                    .distinct();
        }
        it.union(membership)
                .distinct()
                .groupByKey()
                .map(nesting -> {
                    ObjectNode node = mapper.createObjectNode();
                    ArrayNode values = node.putArray(nesting._1());
                    nesting._2().forEach(values::add);
                    return mapper.writeValueAsString(node);
                })
                .saveAsTextFile("build/output.json");

    }
}
