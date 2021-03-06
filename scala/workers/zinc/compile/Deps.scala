package rules_scala
package workers.zinc.compile

import java.math.BigInteger
import java.nio.file.{Files, Path, Paths}
import java.security.MessageDigest

import sbt.internal.inc.{PlainVirtualFile, Relations}

import xsbti.compile.PerClasspathEntryLookup

import workers.common.FileUtil

sealed trait Dep:
  def file: Path
  def classpath: Path
final case class LibraryDep(file: Path) extends Dep:
  override def classpath = file
final case class DepAnalysisFiles(apis: Path, relations: Path)
final case class ExternalDep(file: Path, classpath: Path, analysis: DepAnalysisFiles) extends Dep

object Dep:
  def sha256(file: Path): String =
    val digest = MessageDigest.getInstance("SHA-256")
    BigInteger(1, digest.digest(Files.readAllBytes(file))).toString(16)

  def create(depsCache: Option[Path], classpath: collection.Seq[Path], analyses: Map[Path, (Path, DepAnalysisFiles)]): collection.Seq[Dep] =
    val roots = scala.collection.mutable.Set[Path]()
    classpath.flatMap { original =>
      analyses.get(original).fold[Option[Dep]](Some(LibraryDep(original))) { analysis =>
        val root = analysis._1
        if roots.add(root) then
          depsCache match
            case Some(cacheRoot) =>
              val cachedPath = cacheRoot.resolve(sha256(original))
              FileUtil.extractZipIdempotently(original, cachedPath)
              Some(ExternalDep(original, cachedPath, analysis._2))
            case _ =>
              FileUtil.extractZip(original, root)
              Some(ExternalDep(original, root, analysis._2))
        else None
      }
    }

  def used(deps: Iterable[Dep], relations: Relations, lookup: PerClasspathEntryLookup): Dep => Boolean =
    val externalDeps = relations.allExternalDeps
    val libraryDeps = relations.allLibraryDeps

    {
      case dep: ExternalDep =>
        val definesClass = lookup.definesClass(absoluteVirtualFile(dep))
        externalDeps.exists(definesClass(_))
      case dep: LibraryDep => libraryDeps.contains(absoluteVirtualFile(dep))
    }

  private val root = Paths.get("").toAbsolutePath()
  private def absoluteVirtualFile(dep: Dep): PlainVirtualFile =
    PlainVirtualFile(if dep.file.startsWith(root) then dep.file else root.resolve(dep.file))
