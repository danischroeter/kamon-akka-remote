package akka.remote.kamon.instrumentation.kanela.advisor;

import akka.actor.ExtendedActorSystem;
import kamon.Kamon;
import kamon.akka.RemotingMetrics;
import kanela.agent.libs.net.bytebuddy.asm.Advice;

public class MessageSerializerDeserializeAdvisor {

    @Advice.OnMethodEnter(suppress = Throwable.class)
    public static void onEnter(@Advice.Local("kamonNanos") Long kamonNanos) {
        kamonNanos = Kamon.config().getBoolean("kamon.akka-remote.serialization-metric") ? Kamon.clock().nanos(): -1;
    }

    @Advice.OnMethodExit(suppress = Throwable.class)
    public static void onExit(@Advice.Argument(0) ExtendedActorSystem system, @Advice.Local("kamonNanos") Long kamonNanos) {
        if(kamonNanos >= 0) {
            RemotingMetrics.recordDeserialization(system.name(), Kamon.clock().nanos() - kamonNanos);
        }
    }

}