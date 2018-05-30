package security

import javax.inject.{Inject, Singleton}
import be.objectify.deadbolt.scala.{DeadboltHandler, HandlerKey}
import be.objectify.deadbolt.scala.cache.HandlerCache
import services.UserService

/**
 * @author Steve Chaloner (steve@objectify.be)
 */
@Singleton
class MyHandlerCache @Inject()(userService: UserService) extends HandlerCache {

  val defaultHandler: DeadboltHandler = new MyDeadboltHandler(userService)

  val handlers: Map[Any, DeadboltHandler] = Map(HandlerKeys.defaultHandler -> defaultHandler,
                                                HandlerKeys.altHandler -> new MyDeadboltHandler(userService, Some(MyAlternativeDynamicResourceHandler)),
                                                HandlerKeys.userlessHandler -> new MyUserlessDeadboltHandler(userService))

  override def apply(): DeadboltHandler = defaultHandler

  override def apply(handlerKey: HandlerKey): DeadboltHandler = handlers(handlerKey)
}
