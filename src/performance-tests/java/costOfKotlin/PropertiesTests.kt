package costOfKotlin

import com.natpryce.hamkrest.assertion.assertThat
import com.oneeyedmen.kostings.notProvablyFasterThan
import com.oneeyedmen.kostings.probablyDifferentTo
import com.oneeyedmen.kostings.probablyFasterThan
import costOfKotlin.properties.JavaProperties
import costOfKotlin.properties.KotlinProperties
import org.junit.Test

class PropertiesTests {

@Test
fun java() {
    assertThat(JavaProperties::field_access, ! probablyDifferentTo(JavaProperties::getter))
    assertThat(JavaProperties::field_access,  notProvablyFasterThan(JavaProperties::method_access))
}

@Test
fun kotlin() {
    assertThat(KotlinProperties::method_property, ! probablyDifferentTo(KotlinProperties::constant_method))
    assertThat(KotlinProperties::field_property, probablyFasterThan(KotlinProperties::method_property))
}

@Test
fun mixed() {
    assertThat(KotlinProperties::field_property, probablyFasterThan(JavaProperties::field_access))
    assertThat(KotlinProperties::field_property, probablyFasterThan(JavaProperties::getter))
}

}