package com.olegych.config.beans

import collection.JavaConverters._
import org.yaml.snakeyaml.representer.{Represent, Representer}
import org.yaml.snakeyaml.{DumperOptions, Yaml}
import org.yaml.snakeyaml.constructor.{Construct, Constructor}
import org.yaml.snakeyaml.nodes.{SequenceNode, NodeId, Tag, Node}
import org.yaml.snakeyaml.introspector.{Property, BeanAccess}

/**
 */
class Config {
  def yaml = new Yaml {
    constructor = new Constructor {
      val defaultSeqConstruct = yamlClassConstructors.get(NodeId.sequence)
      yamlClassConstructors.put(NodeId.sequence, new Construct {
        def construct(node: Node) = {
          node match {
            case sn: SequenceNode => if (node.getType == classOf[List[_]]) {
              constructSequence(sn).asScala.toList
            } else {
              if (node.getType == classOf[Option[_]]) {
                constructSequence(sn).asScala.headOption
              } else {
                defaultSeqConstruct.construct(node)
              }
            }
            case _ => defaultSeqConstruct.construct(node)
          }
        }

        def construct2ndStep(node: Node, `object`: AnyRef) {
          defaultSeqConstruct.construct2ndStep(node, `object`)
        }
      })
    }
    representer = new Representer() {
      def fixTag(seq: Iterable[AnyRef], snode: SequenceNode, t: Class[_]) {
        seq.zip(snode.getValue.asScala).foreach {
          case (member, childNode) =>
            if (member != null && t == member.getClass && childNode.getNodeId == NodeId.mapping) {
              childNode.setTag(Tag.MAP)
            }
        }
      }

      override def checkGlobalTag(property: Property, node: Node, o: AnyRef) =
        (o, node, property.getActualTypeArguments) match {
          case (l: Option[AnyRef], snode: SequenceNode, Array(t)) => fixTag(l, snode, t)
          case (l: Iterable[AnyRef], snode: SequenceNode, Array(t)) => fixTag(l, snode, t)
          case _ => super.checkGlobalTag(property, node, o)
        }

      setDefaultFlowStyle(DumperOptions.FlowStyle.AUTO)

      class RepresentList[T <% Iterable[AnyRef]] extends Represent {
        def representData(data: AnyRef): Node = {
          representSequence(getTag(data.getClass, Tag.SEQ), (data.asInstanceOf[T]: Iterable[AnyRef]).asJava,
            null)
        }
      }

      def put[T <% Iterable[AnyRef] : Manifest] = multiRepresenters
        .put(manifest[T].erasure, new RepresentList[T])

      put[Option[AnyRef]]
      put[Seq[AnyRef]]
    }

    setBeanAccess(BeanAccess.FIELD)
    //    val clazz: Class[A] = classOf[A]
    //    println(clazz.getDeclaredMethods.toSeq.mkString("\n"))
    //    println(clazz.getDeclaredConstructors.toSeq.mkString("\n"))
    //    val as = loadAs("a: 1\nb: [7]", clazz)
    //    //    val as = loadAs("a: 1\nb: [7, 1]", clazz)
    //    println(as)
    //    println(dump(load(io.Source.fromFile("d:\\Documents and Settings\\OlegYch\\My " +
    //      "Documents\\Projects\\itr\\payindex\\src\\payindex\\config\\processing.yml").mkString)))
    //    println(dump(as))
    //    println(dump(Map('a -> 1).asJava))
    //    println(dump(Map("a" -> Seq(Map("a" -> 1, "b" -> Seq("ololo asdффы вфыв"), "c" -> None).asJava, 2),
    //      "b" -> 1.0).asJava))
  }

}

//case class A(var a: Int = 5, var b: List[Int] = 1 :: Nil) {
//  def this() = this(7)
//}
//