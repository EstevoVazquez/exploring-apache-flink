package p1;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashSet;

import com.sun.org.apache.bcel.internal.generic.ARETURN;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.util.Collector;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.state.ListState;
import org.apache.flink.api.common.state.ListStateDescriptor;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.streaming.api.datastream.BroadcastStream;
import org.apache.flink.streaming.api.functions.ProcessFunction.Context;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.co.KeyedBroadcastProcessFunction;
import org.apache.flink.streaming.api.functions.co.BroadcastProcessFunction;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.functions.co.BroadcastProcessFunction.ReadOnlyContext;
public class IpAnalyzer {

    public static void main(String[] args) throws Exception {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(TimeCharacteristic.ProcessingTime);

        DataStream<String> data  = env.socketTextStream("localhost", 9090);

        DataStream<Tuple2<String, String>> websites = data.map(row -> {
            String[] fields = row.split(",");
            return new Tuple2<String, String>(fields[4], row);
        }).returns(Types.TUPLE(Types.STRING,Types.STRING));

        DataStream<Tuple2<String, String>> usClicksStream = websites.filter(t -> {
            String[] fields = t.f1.split(",");
            return "US".equals(fields[3]);
        }).returns(Types.TUPLE(Types.STRING,Types.STRING));;

// total number of clicks on every website in separate file
        DataStream<Tuple2<String, Integer>> clicksPerWebsite = usClicksStream
                .map(t -> new Tuple2<String, Integer>(t.f0, 1))
                .returns(Types.TUPLE(Types.STRING,Types.INT))
                .keyBy(0)
                .window(TumblingProcessingTimeWindows.of(Time.seconds(10)))
                .sum(1).returns(Types.TUPLE(Types.STRING,Types.INT));
        clicksPerWebsite.writeAsText("/home/estevo/tfmflink/ipanalyzer/target/us_clicks_per_web.txt");

        DataStream<Tuple2<String, Integer>> maxClicks =	 clicksPerWebsite
                .keyBy(0)
                .window(TumblingProcessingTimeWindows.of(Time.seconds(10)))
                .maxBy(1);
        maxClicks.writeAsText("/home/estevo/tfmflink/ipanalyzer/target/us_max_clicked_web.txt");

        DataStream<Tuple2<String, Integer>> minClicks =
                clicksPerWebsite
                        .keyBy(0)
                        .window(TumblingProcessingTimeWindows.of(Time.seconds(10)))
                        .minBy(1);

        minClicks.writeAsText("/home/estevo/tfmflink/ipanalyzer/target/us_min_clicked_web.txt");


        // execute program
        env.execute("Streaming Click");

    }

}



