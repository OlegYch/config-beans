package com.olegych.config.beans.yaml

import org.yaml.snakeyaml.constructor.{Construct, Constructor}
import org.yaml.snakeyaml.nodes.{Tag, SequenceNode, Node, NodeId}
import org.yaml.snakeyaml.{DumperOptions, Yaml}
import org.yaml.snakeyaml.representer.{Represent, Representer}
import org.yaml.snakeyaml.introspector.{BeanAccess, Property}
import collection.JavaConverters._

/**
 */
class ScalaYaml extends Yaml {
  constructor = new Constructor {
    val defaultSeqConstruct = yamlClassConstructors.get(NodeId.sequence)
    yamlClassConstructors.put(NodeId.sequence, new Construct {
      def construct(node: Node) = {
        def default = defaultSeqConstruct.construct(node)
        node match {
          case sn: SequenceNode => def constructedSeq = constructSequence(sn).asScala
          if (node.getType == classOf[List[_]]) {
            constructedSeq.toList
          } else if (node.getType == classOf[Option[_]]) {
            constructedSeq.headOption
          } else {
            default
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
  }

  setBeanAccess(BeanAccess.FIELD)
}