package arrow.meta.plugin.idea.phases.resolve

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.ProjectComponent
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.AsyncFileListener
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.openapi.vfs.newvfs.events.VFileEvent

/**
 * This project component controls the cache update of MetaSyntheticPackageFragmentProvider
 * It initializes the cache when the project is opened and triggers an update
 * when necessary.
 */
class FragmentProviderComponent(val project: Project) : ProjectComponent, AsyncFileListener, AsyncFileListener.ChangeApplier {

  override fun getComponentName(): String = "arrow.meta.fragmentInitializer"

  override fun initComponent() {
    // dispose listener when project is disposed
    VirtualFileManager.getInstance().addAsyncFileListener(this, project)
  }

  override fun projectOpened() {
    ApplicationManager.getApplication().executeOnPooledThread {
      LOG.debug("Initializing cache of MetaSyntheticPackageFragmentProvider")
      MetaSyntheticPackageFragmentProvider.getInstance(project).computeCache()
    }
  }

  override fun prepareChange(events: MutableList<out VFileEvent>) = this

  override fun beforeVfsChange() {
    LOG.debug("MetaSyntheticPackageFragmentProvider.beforeVfsChange")
  }

  override fun afterVfsChange() {
    LOG.debug("MetaSyntheticPackageFragmentProvider.afterVfsChange")
    MetaSyntheticPackageFragmentProvider.getInstance(project).computeCache()
  }
}
