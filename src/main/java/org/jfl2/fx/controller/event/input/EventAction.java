package org.jfl2.fx.controller.event.input;

import javafx.event.Event;
import javafx.event.EventType;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.jfl2.fx.controller.event.input.trigger.EventTrigger;

import java.util.function.Consumer;


/**
 * 対象Componentと処理内容、トリガーとなるEventTypeを保持する
 */
@Slf4j
@ToString
public class EventAction<T extends Event> {
    // 対象コンポーネント
    @Getter
    protected EventTargetComponentType target;
    // 後続処理を行うならfalse
    @Getter
    protected boolean consume = true;
    // 処理内容
    @Getter
    protected Consumer<Event> operation;
    // トリガ
    @Getter
    protected EventTrigger<T> trigger;

    // 対象コンポーネント管理オブジェクト
//    @Getter
//    protected EventTargetComponentManager targetManager;

    /**
     * このTriggerで処理するならtrueを返す
     *
     * @param event
     * @return
     */
    public boolean isHandle(T event){
        return trigger.isHandle(event);
    }

    /**
     * イベント処理種別取得
     * @return
     */
    public EventType<T> getEventType(){
        return trigger.getEventType();
    }

    /**
     * constructor
     *
     * @param trigger
     * @param target
     * @param operation
     */
    public EventAction(EventTrigger trigger, EventTargetComponentType target, Consumer<Event> operation) {
        this(trigger, target, true, operation);
    }

    /**
     * constructor
     *
     * @param target
     * @param manager
     * @param operation
     */
    public EventAction(EventTargetComponentType target, EventTargetComponentManager manager, Consumer<Event> operation) {
        this(target, manager, true, operation);
    }

    /**
     * constructor
     *
     * @param trigger
     * @param target
     * @param consume
     * @param operation
     */
    public EventAction(EventTrigger trigger, EventTargetComponentType target, boolean consume, Consumer<Event> operation) {
        this.trigger = trigger;
        this.target = target;
        this.consume = consume;
//        this.targetManager = manager;
        this.operation = operation;
    }

    /**
     * constructor
     *
     * @param target
     * @param manager
     * @param consume
     * @param operation
     */
    public EventAction(EventTargetComponentType target, EventTargetComponentManager manager, boolean consume, Consumer<Event> operation) {
        this.target = target;
        this.consume = consume;
//        this.targetManager = manager;
        this.operation = operation;
    }

}
