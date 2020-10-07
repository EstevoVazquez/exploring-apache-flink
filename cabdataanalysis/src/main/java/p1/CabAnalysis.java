package p1;

import com.sun.org.apache.bcel.internal.generic.ARETURN;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.api.java.tuple.Tuple8;


public class CabAnalysis {
    public static void main(String[] args) throws Exception
    {
        // set up the streaming execution environment
        final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();

        ParameterTool params = ParameterTool.fromArgs(args);
        env.getConfig().setGlobalJobParameters(params);

        DataSet<Tuple8<String, String, String, String, Boolean, String, String, Integer>> data = env.readTextFile(params.get("input")).
                map(row ->{
                    String[] words = row.split(",");
                    if(words[4].equalsIgnoreCase("yes")){
                        return new Tuple8<String, String, String, String, Boolean, String, String, Integer>(words[0], words[1], words[2], words[3], Boolean.TRUE, words[5], words[6], Integer.parseInt(words[7]));
                    }
                    return new Tuple8<String, String, String, String, Boolean, String, String, Integer>(words[0], words[1], words[2], words[3], Boolean.FALSE, words[5], words[6], 0);
                }).returns(Types.TUPLE(Types.STRING,Types.STRING,Types.STRING,Types.STRING,Types.BOOLEAN,Types.STRING,Types.STRING, Types.INT)).filter(t-> Boolean.TRUE.equals(t.f4));


        DataSet<Tuple2<String, Integer>> popularDest = data.map(t -> new Tuple2<>(t.f6,t.f7)).returns(Types.TUPLE(Types.STRING,Types.INT))
                .groupBy(0)
                .sum(1)
                .maxBy(1);
       // popularDest.writeAsCsv(params.get("output").concat("/poluar_destination.txt"), "\n", ",");

        DataSet<Tuple2<String, Double>> averagePickup = data.map(t-> new Tuple3<>(t.f5,t.f7,1))
                                                             .returns(Types.TUPLE(Types .STRING,Types.INT,Types.INT))
                                                             .groupBy(0)
                                                             .reduce((c,p) -> new Tuple3<>(c.f0,c.f1 + p.f1,c.f2 + p.f2))
                                                             .map(t -> new Tuple2<>(t.f0,t.f1*1.0/t.f2))
                                                             .returns(Types.TUPLE(Types.STRING,Types.DOUBLE));
      //  averagePickup.writeAsCsv(params.get("output").concat("/avg_pickup.txt"), "\n", ",");
        // execute program
        DataSet<Tuple2<String, Double>> averageDriverPassengerPerTrip =  data.map(t-> new Tuple3<>(t.f3,t.f7,1))
                                                                            .returns(Types.TUPLE(Types.STRING,Types.INT,Types.INT))
                                                                            .groupBy(0)
                                                                            .reduce((c,p) -> new Tuple3<>(c.f0,c.f1 + p.f1,c.f2 + p.f2))
                                                                            .map(t -> new Tuple2<>(t.f0,t.f1*1.0/t.f2))
                                                                            .returns(Types.TUPLE(Types.STRING,Types.DOUBLE));

        averageDriverPassengerPerTrip.writeAsCsv(params.get("output").concat("/avg_drive_passenger_by_trip.txt"), "\n", ",");
        env.execute("Avg Profit Per Month");
    }
}
