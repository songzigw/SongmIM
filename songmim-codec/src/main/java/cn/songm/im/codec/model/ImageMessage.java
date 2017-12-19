/*
 * Copyright [2016] [zhangsong <songm.cn>].
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
 * 
 */
package cn.songm.im.codec.model;

import cn.songm.im.codec.model.Message.Mtype;

/**
 * 图片消息
 * 
 * @author zhangsong
 *
 */
public class ImageMessage extends MessageContent {

    private String path;

    public ImageMessage() {
        super(Mtype.IMAGE);
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "ImageMessage [path=" + path + ","
                + " toString()=" + super.toString()
                + "]";
    }

}
