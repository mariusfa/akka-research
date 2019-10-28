package com.lightbend.akka.sample;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.CompletionStage;

import akka.Done;
import akka.NotUsed;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.javadsl.Source;
import akka.actor.ActorSystem;
import akka.stream.alpakka.file.javadsl.Directory;

public class AkkaQuickstart {

    public static final String SOURCE_FOLDER = "/home/mariufa/tmp/input/";
    public static final String DESTINATION_FOLDER = "/home/mariufa/tmp/output/";
    public static final String METADATA_STORE = "/home/mariufa/tmp/metadata/store.txt";

    public static void main(String[] args) {
        final ActorSystem system = ActorSystem.create("helloakka");
        final Materializer mat = ActorMaterializer.create(system);
        final Source<Path, NotUsed> source = Directory.ls(Paths.get(SOURCE_FOLDER));

        source.runForeach((file) -> System.out.println(file.getFileName()), mat);

        source.runForeach((file) -> moveFile(String.valueOf(file.getFileName())), mat);

        final CompletionStage<Done> done =  source.runForeach((file) -> storeFilename(String.valueOf(file.getFileName())), mat);
        done.thenRun(() -> system.terminate());
    }

    public static void moveFile(String filename) throws IOException {
        Files.move(Paths.get(SOURCE_FOLDER + filename), Paths.get(DESTINATION_FOLDER + filename));
    }

    public static void storeFilename(String filename) throws IOException {
        String stringToStore = filename + "\n";
        Files.write(Paths.get(METADATA_STORE), stringToStore.getBytes(), StandardOpenOption.APPEND);
    }
}
