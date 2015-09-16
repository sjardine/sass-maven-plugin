<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="xml" indent="yes" omit-xml-declaration = "yes" />
	<xsl:decimal-format decimal-separator="." grouping-separator="," />

	<xsl:key name="files" match="file" use="@name" />
	<xsl:template match="checkstyle">
		<h1 id="top">scss-lint report</h1>
		<div class="section">
			<h2>Summary</h2>
			<!-- Summary part -->
			<xsl:apply-templates select="." mode="summary" />
		</div>
		<div class="section">
			<h2>Files</h2>
			<!-- Package List part -->
			<xsl:apply-templates select="." mode="filelist" />
		</div>
		<div class="section">
			<!-- For each package create its part -->
			<h2>Details</h2>
			<xsl:apply-templates select="file[@name and generate-id(.) = generate-id(key('files', @name))]" />
		</div>
	</xsl:template>

	<xsl:template match="checkstyle" mode="filelist">
		<table class="table table-striped">
			<tr>
				<th>Name</th>
				<th>Errors</th>
				<th>Warnings</th>
				<th>Infos</th>
			</tr>
			<xsl:for-each select="file[@name and generate-id(.) = generate-id(key('files', @name))]">
				<xsl:sort data-type="number" order="descending" select="count(key('files', @name)/error[@severity='error'])"/>
				<xsl:sort data-type="number" order="descending" select="count(key('files', @name)/error[@severity='warning'])"/>
				<xsl:sort data-type="number" order="descending" select="count(key('files', @name)/error[@severity='info'])"/>
				<xsl:variable name="errorCount" select="count(key('files', @name)/error[@severity='error'])"/>
				<xsl:variable name="warningCount" select="count(key('files', @name)/error[@severity='warning'])"/>
				<xsl:variable name="infoCount" select="count(key('files', @name)/error[@severity='info'])"/>
				<tr>
					<xsl:call-template name="alternated-row"/>
					<td>
						<a href="#f-{translate(@name,'\','/')}">
							<xsl:value-of select="@name"/>
						</a>
					</td>
					<td>
						<xsl:value-of select="$errorCount"/>
					</td>
					<td>
						<xsl:value-of select="$warningCount"/>
					</td>
					<td>
						<xsl:value-of select="$infoCount"/>
					</td>
				</tr>
			</xsl:for-each>
		</table>
	</xsl:template>

	<xsl:template match="file">
		<!--a name="f-{translate(@name,'\','/')}"></a-->
		<h3 id="f-{translate(@name,'\','/')}">
			<xsl:value-of select="@name"/>
		</h3>
		<table class="table table-striped">
			<tr>
				<th>Severity</th>
				<th>Error Description</th>
				<th>Line</th>
			</tr>
			<xsl:for-each select="key('files', @name)/error">
				<xsl:sort data-type="number" order="ascending" select="@line"/>
				<tr>
					<xsl:call-template name="alternated-row"/>
					<td>
						<xsl:value-of select="@severity"/>
					</td>
					<td>
						<xsl:value-of select="@message"/>
					</td>
					<td>
						<xsl:value-of select="@line"/>
					</td>
				</tr>
			</xsl:for-each>
		</table>
		<a href="#top">Back to top</a>
	</xsl:template>

	<xsl:template match="checkstyle" mode="summary">
		<xsl:variable name="fileCount" select="count(file[@name and generate-id(.) = generate-id(key('files', @name))])"/>
		<xsl:variable name="errorCount" select="count(file/error[@severity='error'])"/>
		<xsl:variable name="warningCount" select="count(file/error[@severity='warning'])"/>
		<xsl:variable name="infoCount" select="count(file/error[@severity='info'])"/>
		<table class="table table-striped">
			<tr>
				<th>Files</th>
				<th>Errors</th>
				<th>Warnings</th>
				<th>Infos</th>
			</tr>
			<tr>
				<xsl:call-template name="alternated-row"/>
				<td>
					<xsl:value-of select="$fileCount"/>
				</td>
				<td>
					<xsl:value-of select="$errorCount"/>
				</td>
				<td>
					<xsl:value-of select="$warningCount"/>
				</td>
				<td>
					<xsl:value-of select="$infoCount"/>
				</td>
			</tr>
		</table>
	</xsl:template>

	<xsl:template name="alternated-row">
		<xsl:attribute name="class">
			<xsl:if test="position() mod 2 = 1">a</xsl:if>
			<xsl:if test="position() mod 2 = 0">b</xsl:if>
		</xsl:attribute>
	</xsl:template>
</xsl:stylesheet>
