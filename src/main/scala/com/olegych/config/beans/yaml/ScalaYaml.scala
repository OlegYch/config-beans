package com.olegych.config.beans.yaml

import org.yaml.snakeyaml.{DumperOptions, Yaml}
import org.yaml.snakeyaml.representer.{Represent, Representer}
import org.yaml.snakeyaml.introspector.{BeanAccess, Property}
import collection.JavaConverters._
import org.yaml.snakeyaml.composer.Composer
import org.yaml.snakeyaml.serializer.Serializer
import org.yaml.snakeyaml.emitter.Emitter
import org.yaml.snakeyaml.nodes._
import org.yaml.snakeyaml.constructor.{AbstractConstruct, Construct, Constructor}

/**
 */
class ScalaYaml extends Yaml {
  constructor = new Constructor {
    val defaultStrConstructor = yamlConstructors.get(Tag.STR)
    yamlConstructors.put(Tag.STR, new AbstractConstruct {
      def construct(node: Node) = {
        val str = defaultStrConstructor.construct(node)
        if (node.getType == classOf[Symbol]) {
          Symbol(str.asInstanceOf[String])
        } else {
          str
        }
      }
    })
    val defaultSeqConstruct = yamlClassConstructors.get(NodeId.sequence)
    yamlClassConstructors.put(NodeId.sequence, new Construct {
      def construct(node: Node) = {
        def default = defaultSeqConstruct.construct(node)
        node match {
          case sn: SequenceNode => def constructedSeq = constructSequence(sn).asScala
          if (classOf[Seq[_]].isAssignableFrom(node.getType)) {
            constructedSeq.toList
          } else {
            if (node.getType == classOf[Option[_]]) {
              constructedSeq.headOption
            } else {
              default
            }
          }
          case _ => default
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
        case (l: Seq[AnyRef], snode: SequenceNode, Array(t)) => fixTag(l, snode, t)
        case _ => super.checkGlobalTag(property, node, o)
      }

    setDefaultFlowStyle(DumperOptions.FlowStyle.AUTO)

    class RepresentList[T <% Iterable[AnyRef]] extends Represent {
      def representData(data: AnyRef): Node = {
        representSequence(getTag(data.getClass, Tag.SEQ), (data.asInstanceOf[T]: Iterable[AnyRef]).asJava,
          null)
      }
    }

    def put[T <% Iterable[AnyRef] : Manifest] =
      multiRepresenters.put(manifest[T].erasure, new RepresentList[T])

    put[Option[AnyRef]]
    put[Seq[AnyRef]]
    representers.put(classOf[Symbol], new Represent {
      def representData(data: AnyRef) = representScalar(org.yaml.snakeyaml.nodes.Tag.STR,
        data.asInstanceOf[Symbol].name)
    })
  }

  setBeanAccess(BeanAccess.FIELD)

  def construct[T: Manifest](n: Node): T = {
    constructor.setComposer(new Composer(null, null) {
      override def getSingleNode = n
    })
    constructor.getSingleData(manifest[T].erasure).asInstanceOf[T]
  }

  def dump(node: Node, output: java.io.Writer, rootTag: Tag) {
    resource
      .managed(new Serializer(new Emitter(output, dumperOptions), resolver, dumperOptions, rootTag))
      .acquireAndGet {s => s.open(); s.serialize(node)}
  }

  //  @tailrec final def traverse(d: Node, rest:Seq[Node] = Nil):Seq[Node] = {
  //    (d match {
  //      case m: MappingNode => m.getValue.asScala.flatMap(t => Seq(t.getKeyNode) ++ rest ++ traverse(t
  // .getValueNode, rest))
  //      case s: SequenceNode => s.getValue.asScala.flatMap(t => traverse(t, rest))
  //      case other => Nil
  //    }) ++ rest
  //  }.map(n => {
  //    val withValue = n.asInstanceOf[ {var value: String}]
  ////    withValue.value = withValue.value + "-default"
  //    n
  //  } )
}