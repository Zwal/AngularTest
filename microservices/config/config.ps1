# Copyright (C) 2017 Juergen Zimmermann, Hochschule Karlsruhe
#
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program.  If not, see <http://www.gnu.org/licenses/>.

# Aufruf:   config [cmd=start|stop|shutdown]

param (
    [string]$cmd = "start"
)

# Titel setzen
$script = $myInvocation.MyCommand.Name
$host.ui.RawUI.WindowTitle = $script

function StartServer {
    $Truststore = "-Djavax.net.ssl.trustStore=src/test/resources/truststore.p12"
    $TruststorePassword = "-Djavax.net.ssl.trustStorePassword=zimmermann"
    ./gradlew --build-cache $Truststore $TruststorePassword
}

function StopServer {
    $Schema = "http"
    $Port = "8888"
    $URL = "${SCHEMA}://localhost:${PORT}/application/shutdown"

    C:\Zimmermann\Git\mingw64\bin\curl -v -H "Content-Type:application/json" `
        -d "{}" --basic -u admin:p --http2 --tlsv1.2 -k $URL
}

switch ($cmd) {
    "start" { StartServer; break }
    "stop" { StopServer; break }
    "shutdown" { StopServer; break }
    default { write-host "$script [cmd=start|stop|shutdown]" }
}
