/*
 * Copyright 2019 the original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.hauner.openapi.spring.converter.schema

import com.github.hauner.openapi.spring.converter.mapping.MappingLevel
import com.github.hauner.openapi.spring.converter.mapping.TypeMappingX


interface SchemaType {

    List<TypeMappingX> matchEndpointMapping (List<TypeMappingX> typeMappings)
    List<TypeMappingX> matchIoMapping (List<TypeMappingX> typeMappings)
    List<TypeMappingX> matchTypeMapping (List<TypeMappingX> typeMappings)

}

abstract class SchemaTypeBase implements SchemaType {

    protected SchemaInfo info

    SchemaTypeBase(SchemaInfo info) {
        this.info = info
    }

    @Override
    List<TypeMappingX> matchEndpointMapping (List<TypeMappingX> typeMappings) {
        // mappings matching by path
        List<TypeMappingX> endpoint = typeMappings.findAll {
            it.isLevel (MappingLevel.ENDPOINT) && it.matches (info)
        }.collect {
            it.childMappings
        }.flatten () as List<TypeMappingX>

        // io mappings
        List<TypeMappingX> io = endpoint.findAll {
            it.isLevel (MappingLevel.IO) && it.matches (info)
        }.collect {
            it.childMappings
        }.flatten () as List<TypeMappingX>

        if (!io.empty) {
            return io
        }

        // type mappings
        endpoint.findAll {
            it.isLevel (MappingLevel.TYPE) && it.matches (info)
        }.collect {
            it.childMappings
        }.flatten () as List<TypeMappingX>
    }

    List<TypeMappingX> matchIoMapping (List<TypeMappingX> typeMappings) {
        // io mappings
        typeMappings.findAll {
            it.isLevel (MappingLevel.IO) && it.matches (info)
        }.collect {
            it.childMappings
        }.flatten () as List<TypeMappingX>
    }

}

class ObjectSchemaType extends SchemaTypeBase {

    ObjectSchemaType (SchemaInfo info) {
        super (info)
    }

    @Override
    List<TypeMappingX> matchTypeMapping (List<TypeMappingX> typeMappings) {
        typeMappings.findAll {
            it.isLevel (MappingLevel.TYPE) && it.matches (info)
        }
    }

}

class ArraySchemaType extends SchemaTypeBase {

    ArraySchemaType (SchemaInfo info) {
        super (info)
    }

    @Override
    List<TypeMappingX> matchTypeMapping (List<TypeMappingX> typeMappings) {
        typeMappings.findAll () {
            it.isLevel (MappingLevel.TYPE) && it.matches (new SchemaInfo (null, null,'array'))
        }
    }

}

