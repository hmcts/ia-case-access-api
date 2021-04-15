variable "product" {}

variable "raw_product" {
  default = "ia"
}

variable "component" {}

variable "location" {
  default = "UK South"
}

variable "env" {}

variable "subscription" {}

variable "deployment_namespace" {}

variable "common_tags" {
  type = map(string)
}

variable "postgresql_user" {
  default = "ia_tes"
}
