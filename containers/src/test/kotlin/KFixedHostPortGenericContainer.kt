import org.testcontainers.containers.FixedHostPortGenericContainer

class KFixedHostPortGenericContainer(imageName: String) :
    FixedHostPortGenericContainer<KFixedHostPortGenericContainer>(imageName) {
    fun pause() {
        dockerClient.pauseContainerCmd(getContainerId()).exec()
    }

    fun unpause() {
        dockerClient.unpauseContainerCmd(getContainerId()).exec()
    }
}
