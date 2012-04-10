package com.olegych.config.beans.yaml

import collection.JavaConversions._
import org.yaml.snakeyaml.nodes._

/**
 */
class YamlTrees {
  def merge(left: Option[Node], right: Option[Node])(onConflict: ((Node, Node) => Unit)) {
    (left, right) match {
      case (None, None) => None
      case (None, Some(node)) => node
      case (Some(node), None) => node
      case (Some(left: CollectionNode), Some(right: CollectionNode)) => (left, right) match {
        case (left: MappingNode, right: MappingNode) => {
          left.getValue.zipAll(right.getValue, null, null).foreach {
            case (left, right) => {
              def key(n: NodeTuple) = Option(n).map(_.getKeyNode)
              def value(n: NodeTuple) = Option(n).map(_.getValueNode)
              merge(key(left), key(right))(onConflict)
              merge(value(left), value(right))(onConflict)
            }
          }
        }
        case (left: SequenceNode, right: SequenceNode) => {
          left.getValue.zipAll(right.getValue, null, null).foreach {
            case (left, right) => merge(Option(left), Option(right))(onConflict)
          }
        }
        case (left, right) => sys.error("Could not merge %s and %s".format(left, right))
      }
      case (Some(left), Some(right)) => onConflict(left, right)
    }
  }
}
