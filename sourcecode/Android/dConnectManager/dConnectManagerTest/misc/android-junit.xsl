<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
    xmlns:lxslt="http://xml.apache.org/xslt"
    xmlns:redirect="http://xml.apache.org/xalan/redirect"
    extension-element-prefixes="redirect">

    <xsl:param name="output.dir" select="'report/suites'"/>
    <xsl:output method="xml" indent="yes" encoding="US-ASCII"/>

    <xsl:template match="testsuite" >
        <xsl:variable name="name" select="@name" />
        <xsl:variable name="tests" select="count(testcase)" />
        <xsl:variable name="errors" select="count(error)" />
        <xsl:variable name="failures" select="count(failure)" />
        <xsl:variable name="time" select="sum(testcase/@time)" />

        <redirect:write file="{$output.dir}/TEST-{$name}.xml" >
            <testsuite name="{$name}" time="{$time}" tests="{$tests}" errors="{$errors}" failures="{$failures}">
                <xsl:apply-templates select="testcase" />
            </testsuite>
        </redirect:write>
    </xsl:template>

    <xsl:template match="testcase">
        <xsl:variable name="name" select="@name" />
        <xsl:variable name="classname" select="@classname" />
        <xsl:variable name="time" select="@time" />
		<testcase name="{$name}" classname="{$classname}" time="{$time}" />
	</xsl:template>

</xsl:stylesheet>
