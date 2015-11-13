package org.jfl2.fx.controller.event.input;

import javafx.scene.Node;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Slf4j
public class EventTargetComponentManager {

    /**
     * NodeとTypeの対応表
     */
    Map<Node, Set<EventTargetComponentType>> node2types = new HashMap<>();
    Map<EventTargetComponentType, Set<Node>> type2node = new HashMap<>();

    public void addEvent(EventAction event) {
        Set<Node> nodes = type2node.get(event.getTarget());
        if (nodes != null) {
            for (Node node : nodes) {
                node.addEventFilter(event.getEventType(), (ev) -> {
                    if (event.isHandle(ev) && !ev.isConsumed() ) {
                        event.getOperation().accept(ev);
                        if (event.isConsume()) {
//                            log.debug("Consumed " + ev.isConsumed());
                            ev.consume();
                        }
                    }
                });
            }
        }
    }

    /**
     * type と nodeが関連付けられているならtrueを返す
     * ただし、ANYの場合は無条件でtrue
     *
     * @param node JavaFXのNode
     * @param type ComponentType
     * @return
     */
    public boolean isEventTarget(Node node, EventTargetComponentType type) {
        if (EventTargetComponentType.ANY.equals(type)) {
            return true;
        }

        Set<EventTargetComponentType> types = node2types.get(node);
        if (types != null) {
            return types.contains(type);
        }
        return false;
    }

    /**
     * node に type を追加する
     *
     * @param node
     * @param types
     * @return
     */
    public EventTargetComponentManager addNode(Node node, EventTargetComponentType... types) {
        Set<EventTargetComponentType> typeSet = node2types.get(node);
        if (typeSet == null) {
            typeSet = new HashSet<>();
            node2types.put(node, typeSet);
        }

        for (EventTargetComponentType type : types) {
            typeSet.add(type);
            Set<Node> nodeSet = type2node.get(type);
            if (nodeSet == null) {
                nodeSet = new HashSet<>();
                type2node.put(type, nodeSet);
            }
            nodeSet.add(node);
        }
        return this;
    }

}
