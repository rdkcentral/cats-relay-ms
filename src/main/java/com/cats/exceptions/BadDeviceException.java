package com.cats.exceptions;

/*
 * Copyright 2021 Comcast Cable Communications Management, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

/**
 * Customised Exception for failures due to a Bad Device.
 * */
public class BadDeviceException extends RuntimeException{
	public BadDeviceException()
    {
        super();
    }

    public BadDeviceException(String message )
    {
        super( message );
    }

    public BadDeviceException(Throwable cause )
    {
        super( cause );
    }

    public BadDeviceException(String message, Throwable cause )
    {
        super( message, cause );
    }
}
