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
package cn.songm.im.model.message;

import cn.songm.im.model.message.Message.Mtype;

/**
 * 文字消息
 * 
 * @author zhangsong
 *
 */
public class TextMessage extends MessageContent {

    private String text;

    public TextMessage(String t) {
        super(Mtype.TEXT);
        this.text = t;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((text == null) ? 0 : text.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        TextMessage other = (TextMessage) obj;
        if (text == null) {
            if (other.text != null) return false;
        } else if (!text.equals(other.text)) return false;
        return true;
    }

    @Override
    public String toString() {
        return "TextMessage [text=" + text + ", " + super.toString() + "]";
    }

}
