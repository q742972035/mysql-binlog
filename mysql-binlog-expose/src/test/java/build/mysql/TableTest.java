package build.mysql;
import com.github.q742972035.mysql.binlog.expose.build.mysql.table.IndexTable;
import com.github.q742972035.mysql.binlog.expose.build.mysql.table.Table;
import com.github.q742972035.mysql.binlog.expose.build.mysql.table.TableHelper;
import org.junit.Test;
import reactor.obj.Animal;

import static org.assertj.core.api.Assertions.*;

/**
 * @program: mysql-binlog-incr-expose
 * @description
 * @author: zy
 * @create: 2019-08-19 17:00
 **/
public class TableTest {

    @Test
    public void test() throws IllegalAccessException, InstantiationException {
        Table table = new IndexTable(Animal.class);
        Object obj1 = table.createTable();
        Object obj2 = table.createTable();

        assertThat(obj1).isEqualTo(obj2);

        assertThat(table.getTableColumns().size()).isEqualTo(3);
        assertThat(table.getTableColumns().get(0).getFieldName()).isEqualTo("name");
        assertThat(table.getTableColumns().get(1).getFieldName()).isEqualTo("age");
        assertThat(table.getTableColumns().get(2).getFieldName()).isEqualTo("isAnimal");

        assertThat(table.getTableColumns().get(0).getType()).isEqualTo(String.class);
        assertThat(table.getTableColumns().get(1).getType()).isEqualTo(Integer.TYPE);
        assertThat(table.getTableColumns().get(2).getType()).isEqualTo(Boolean.TYPE);
    }


    @Test
    public void tableHeplerTest() throws InstantiationException, IllegalAccessException {
        Table table = new IndexTable(Animal.class);
        TableHelper tableHelper = new TableHelper(table);

        tableHelper.set(1,"小红");
        tableHelper.set(2,99);
        tableHelper.set(3,true);

        Animal animal = (Animal) table.createTable();

        assertThat(animal.getName()).isEqualTo("小红");
        assertThat(animal.getAge()).isEqualTo(99);
        assertThat(animal.isAnimal()).isEqualTo(true);
    }

}
