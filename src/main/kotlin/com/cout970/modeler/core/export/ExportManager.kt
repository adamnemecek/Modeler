package com.cout970.modeler.core.export

import com.cout970.modeler.PathConstants
import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.core.export.project.ProjectLoaderV10
import com.cout970.modeler.core.export.project.ProjectLoaderV11
import com.cout970.modeler.core.log.Level
import com.cout970.modeler.core.log.log
import com.cout970.modeler.core.log.print
import com.cout970.modeler.core.model.selection.ClipboardNone.Companion.model
import com.cout970.modeler.core.project.ProjectManager
import com.cout970.modeler.core.project.ProjectProperties
import com.cout970.modeler.core.resource.ResourceLoader
import com.cout970.modeler.gui.Gui
import com.cout970.modeler.gui.event.Notification
import com.cout970.modeler.gui.event.NotificationHandler
import com.cout970.modeler.util.createParentsIfNeeded
import com.google.gson.GsonBuilder
import java.io.File
import java.util.zip.ZipFile

/**
 * Created by cout970 on 2017/01/02.
 */
class ExportManager(val resourceLoader: ResourceLoader) {

    companion object {
        const val CURRENT_SAVE_VERSION = "1.1"
        val VERSION_GSON = GsonBuilder().create()!!
    }

    fun loadProject(path: String): ProgramSave {
        val zip = ZipFile(path)

        val version = zip.load<String>("version.json", VERSION_GSON)
                      ?: throw IllegalStateException("Missing file 'version.json' inside '$path'")

        return when (version) {
            "1.0" -> ProjectLoaderV10.loadProject(zip, path)
            "1.1" -> ProjectLoaderV11.loadProject(zip, path)
            else -> throw IllegalStateException("Invalid save version $version")
        }
    }

    fun saveProject(path: String, save: ProgramSave) {
        File(path).createParentsIfNeeded()
        ProjectLoaderV11.saveProject(path, save)
    }

    fun saveProject(path: String, manger: ProjectManager) {
        saveProject(path, ProgramSave(CURRENT_SAVE_VERSION, manger.projectProperties, manger.model))
    }

    fun loadLastProjectIfExists(projectManager: ProjectManager, gui: Gui) {
        val path = File(PathConstants.LAST_BACKUP_FILE_PATH)
        if (path.exists()) {
            try {
                log(Level.FINE) { "Found last project, loading..." }
                val save = loadProject(path.path)
                projectManager.loadProjectProperties(save.projectProperties)
                projectManager.updateModel(save.model)
                gui.windowHandler.updateTitle(save.projectProperties.name)
                model.materials.forEach { it.loadTexture(resourceLoader) }
                log(Level.FINE) { "Last project loaded" }
                NotificationHandler.push(Notification("Project loaded",
                        "Loaded project from last execution"))
            } catch (e: Exception) {
                log(Level.ERROR) { "Unable to load last project" }
                e.print()
                NotificationHandler.push(Notification("Error loading project",
                        "Unable to load project at '${path.absolutePath}': $e"))
            }
        } else {
            log(Level.FINE) { "No last project found, ignoring" }
        }
    }
}

data class ProgramSave(val version: String, val projectProperties: ProjectProperties, val model: IModel)