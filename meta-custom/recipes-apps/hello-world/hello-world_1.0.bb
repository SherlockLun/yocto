SUMMARY = "Hello World Application"
DESCRIPTION = "A simple Hello World app for testing Yocto"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade40b6dfe2b11f1d8b6f6c09b77"

SRC_URI = "file://hello.c"
S = "${WORKDIR}"

do_compile() {
    ${CC} ${CFLAGS} ${LDFLAGS} hello.c -o hello
}

do_install() {
    install -d ${D}${bindir}
    install -m 0755 hello ${D}${bindir}
}

FILES:${PN} = "${bindir}/hello"
