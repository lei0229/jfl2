package org.jfl2.fx.control.cell;

import javafx.beans.NamedArg;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * テーブルのプロパティ値記述用
 */
@Slf4j
public class Jfl2PropertyValueFactory<S,T> extends PropertyValueFactory<S,T> {
    /**
     * Creates a default PropertyValueFactory to extract the value from a given
     * TableView row item reflectively, using the given property name.
     *
     * @param property The name of the property with which to attempt to
     *                 reflectively extract a corresponding value for in a given object.
     */
    public Jfl2PropertyValueFactory(@NamedArg("property") String property) {
        super(property);
    }

    /**
     * 表示値取得メソッド
     * @param param
     * @return
     */
    @Override
    public ObservableValue<T> call(TableColumn.CellDataFeatures<S,T> param) {
        return super.call(param);
        /*
        ObservableValue<T> orgValue = super.call(param);
        T value = orgValue.getValue();
        if(String.class.isInstance(value)){
            try {
                value.getClass().newInstance();
                Constructor constructor = value.getClass().getConstructor(new Class[]{String.class});
                T newValue = (T) constructor.newInstance("#" + value.toString() );
                return new ReadOnlyObjectWrapper<T>(newValue);
            } catch (InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
                log.error("Value changing is failed.", e);
            }
        }
        return orgValue;
        */
    }
}
