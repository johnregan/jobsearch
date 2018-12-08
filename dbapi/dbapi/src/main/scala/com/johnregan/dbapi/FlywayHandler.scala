package com.johnregan.dbapi

import cats.effect.{Effect, IO}
import com.typesafe.scalalogging.LazyLogging
import javax.sql.DataSource
import org.flywaydb.core.Flyway

class FlywayHandler[F[_]](ds: DataSource)(implicit F: Effect[F]) extends LazyLogging {

  def performMigration = F.liftIO {
    for {
      flyway <- IO {
        Flyway.configure()
          .table("schema_version")
          .dataSource(ds)
          .load()
      }
      numberOfMigrations <- IO(flyway.migrate)
    } yield {
      numberOfMigrations
    }
  }
}