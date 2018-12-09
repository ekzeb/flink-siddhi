package com.github.haoch.experimental

import akka.NotUsed
import akka.actor.{Actor, ActorRefFactory, ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.xml.ScalaXmlSupport
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.duration._
import akka.stream.{ActorMaterializer, Materializer}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

object Bootstrap extends App with CEPService {

  // we need an ActorSystem to host our application in
  implicit val system = ActorSystem("on-spray-can")
  implicit val mat = ActorMaterializer()


  // create and start our service actor
  //val service = system.actorOf(Props[CEPServiceActor], "control-service")

  implicit val timeout = Timeout(5.seconds)

  // start a new HTTP server on port 8080 with our service actor as the handler
  //IO(Http) ? Http.Bind(service, interface = "localhost", port = 8080)
  Http().bindAndHandle(route, "0.0.0.0", port = 8080)
}

/*// we don't implement our route structure directly in the service actor because
// we want to be able to test it independently, without having to spin up an actor
class CEPServiceActor extends Actor with CEPService {

  // the HttpService trait defines only one abstract member, which
  // connects the services environment to the enclosing actor or test
  def actorRefFactory = context

  // this actor only runs our route, but you could add
  // other things here, like request stream processing
  // or timeout handling
  def receive = runRoute(route)
}*/

// this trait defines our service behavior independently from the service actor
trait CEPService extends HttpService with ScalaXmlSupport {

  val route: Route =
    get {
      pathSingleSlash {
        complete {
          <html>
            <body>
              <h1>Say hello to
                <i>akka-http-routing</i>
                on
                <i>akka-core</i>
                !</h1>
            </body>
          </html>
        }
      }
    }

    /*// JSON paths ??? which USE content type negotiation due `respondWithMediaType(MediaType)` IS ANTI-PATTERN !
    path("/api/v1/queries/:id") {
      put {
        //respondWithMediaType(`application/json`) {
          complete {
            ???
          }
        //}
      }
      delete {
        //respondWithMediaType(`application/json`) {
          ???
        //}
      }
    }

    path("/api/v1/queries") {
      get {
        //respondWithMediaType(`application/json`) {
          complete {
            ???
          }
        //}
      }
      post {
        //respondWithMediaType(`application/json`) {
          complete {
            ???
          }
        //}
      }
    }
*/
}

trait HttpService {
  implicit def system: ActorRefFactory
  implicit def mat: Materializer
}