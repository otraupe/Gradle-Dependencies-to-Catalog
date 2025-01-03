package com.opappdevs.depstocatalog

import com.opappdevs.depstocatalog.domain.converter.DependencyConverter
import kotlin.test.Test
import kotlin.test.assertEquals

class ConversionTests {

    private val adep = "tld.org.agroup:aname:aversion"
    private val bdep = "tld.org.bgroup:bname:bversion"
    private val cdep = "tld.org.cgroup:cname:cversion"
    private val ddep = "tld.org.dgroup:dname:dversion"
    private val edep = "tld.org.egroup:ename:eversion"

    private val aresult = """
        agroup = "aversion"
        org-agroup-aname = { group = "tld.org.agroup", name = "aname", version.ref = "agroup" }
        implementation(libs.org.agroup.aname)
    """.trimIndent()
    private val bresult = """
        bgroup = "bversion"
        org-bgroup-bname = { group = "tld.org.bgroup", name = "bname", version.ref = "bgroup" }
        implementation(libs.org.bgroup.bname)
    """.trimIndent()
    private val cresult = """
        cgroup = "cversion"
        org-cgroup-cname = { group = "tld.org.cgroup", name = "cname", version.ref = "cgroup" }
        implementation(libs.org.cgroup.cname)
    """.trimIndent()
    private val dresult = """
        dgroup = "dversion"
        org-dgroup-dname = { group = "tld.org.dgroup", name = "dname", version.ref = "dgroup" }
        implementation(libs.org.dgroup.dname)
    """.trimIndent()
    private val eresult = """
        egroup = "eversion"
        org-egroup-ename = { group = "tld.org.egroup", name = "ename", version.ref = "egroup" }
        implementation(libs.org.egroup.ename)
    """.trimIndent()

    @Test
    fun detectKotlinDependency() {

    }

    @Test
    fun convertKotlinDependency() {
        val adep = "implementation(\"tld.org.agroup:aname:aversion\")"

        //assertEquals(DependencyConverter.convert(adep), aresult)
    }

    // TODO: collect same version number, but only if group identical
    // TODO: one letter different -> separate definition/declaration
    // TODO: no blank lines in output
    // TODO: unmatched text (e.g. in first line) mustn't disappear
    // TODO: test all single conditions
    // TODO: test 1-5 same format dependencies
    // TODO: test format mixture with various blank lines, unmatched text, and multiple definitions a line (chaos monkey)
    // TODO: do any white spaces get removed from the three variables?

}