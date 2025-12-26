import { mat3, mat4, vec3, vec4 } from "gl-matrix";
import { addPointExternal } from "./canvas";


const vsSource = `
    attribute vec4 vertPos;

    void main() {
        gl_Position = vertPos;
    }
`;

const fsSource = `
    precision highp float;

    uniform mat4 rotationMatrix;
    uniform sampler2D sampler;

    float sphere(vec3 ray, vec3 dir, vec3 center, float radius) {
        vec3 rc = ray - center;
        float a = dot(dir, dir);
        float b = 2.0 * dot(dir, rc);
        float c = dot(rc, rc) - (radius * radius);
    
        float discr = b * b - 4.0 * a * c;
        if (discr < 0.0) {
            return -1.0;
        } else {
            return (-b - sqrt(discr)) / (2.0 * a);
        }
    }

    void main() {
        // gl_FragColor = texture2D(sampler, gl_FragCoord.xy / vec2(512.0, 512.0));
        // return;

        // Разрешение холста
        vec2 resolution = vec2(512.0, 512.0);

        // Соотношение сторон
        float aspectRatio = resolution.y / resolution.x;

        // Нормализация координат пикселя холста
        vec2 uv = (-1.0 + 2.0 * gl_FragCoord.xy / resolution.xy) * vec2(aspectRatio, 1.0);

        vec3 ro = vec3(0.0, 0.0, 0.0);
        vec3 rd = normalize(vec3(uv, 70.0));
        vec3 p = vec3(0.0, 0.0, 90.0);
        float radius = 1.0;
        float t = sphere(ro, rd, p, radius);

        float gray = 0.75;

        if (t > 0.0) {
            vec3 hit = (rotationMatrix * vec4(ro + t * rd - p, 1.0)).xyz;
            if (hit.z < 0.0) {
                gl_FragColor = mix(
                    texture2D(sampler, hit.xy / 2.5 - vec2(0.5, 0.5)),
                    vec4(vec3(gray), 1.0),
                    max(0.0, 1.0 - abs(hit.z) * 3.0));

                return;
            }

            gl_FragColor = vec4(vec3(mix(gray, (1.0 - hit.z) / 1.5, hit.z / 2.5)), 1.0);
        } else {
            gl_FragColor = vec4(uv.x, -uv.y, 1.0, 1.0);
        }
    }
`;

let isMouseDown = false;
let angleX = 0;
let angleY = 0;

const loadShader = (gl, type, source) => {
    const shader = gl.createShader(type);

    gl.shaderSource(shader, source);
    gl.compileShader(shader);

    if (!gl.getShaderParameter(shader, gl.COMPILE_STATUS)) {
        console.log(
            `An error occurred compiling the shaders: ${gl.getShaderInfoLog(shader)}`,
        );
        gl.deleteShader(shader);

        return null;
    }

    return shader;
};

const initShaderProgram = (gl, vsSource, fsSource) => {
    const vertexShader = loadShader(gl, gl.VERTEX_SHADER, vsSource);
    const fragmentShader = loadShader(gl, gl.FRAGMENT_SHADER, fsSource);

    const shaderProgram = gl.createProgram();
    gl.attachShader(shaderProgram, vertexShader);
    gl.attachShader(shaderProgram, fragmentShader);
    gl.linkProgram(shaderProgram);

    if (!gl.getProgramParameter(shaderProgram, gl.LINK_STATUS)) {
        console.log(
            `Unable to initialize the shader program: ${gl.getProgramInfoLog(
                shaderProgram,
            )}`,
        );

        return null;
    }

    return shaderProgram;
};

const initPositionBuffer = (gl) => {
    const positionBuffer = gl.createBuffer();
    gl.bindBuffer(gl.ARRAY_BUFFER, positionBuffer);

    const positions = [1.0, 1.0, -1.0, 1.0, 1.0, -1.0, -1.0, -1.0];
    gl.bufferData(gl.ARRAY_BUFFER, new Float32Array(positions), gl.STATIC_DRAW);

    return positionBuffer;
}

const initBuffers = (gl) => {
    const positionBuffer = initPositionBuffer(gl);

    return {
        position: positionBuffer,
    };
}

const setPositionAttribute = (gl, buffers, programInfo) => {
    const numComponents = 2;
    const type = gl.FLOAT;
    const normalize = false;
    const stride = 0;
    const offset = 0;
    gl.bindBuffer(gl.ARRAY_BUFFER, buffers.position);
    gl.vertexAttribPointer(
        programInfo.attribLocations.vertexPosition,
        numComponents,
        type,
        normalize,
        stride,
        offset,
    );
    gl.enableVertexAttribArray(programInfo.attribLocations.vertexPosition);
}

const initTexture = (gl) => {
    return gl.createTexture();
};

const fillTexture = (gl, canvasTexture) => {
    const canvas = document.getElementById("graph");

    gl.bindTexture(gl.TEXTURE_2D, canvasTexture);

    gl.pixelStorei(gl.UNPACK_FLIP_Y_WEBGL, true);
    gl.texImage2D(gl.TEXTURE_2D, 0, gl.RGBA, gl.RGBA, gl.UNSIGNED_BYTE, canvas);
    gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_MAG_FILTER, gl.LINEAR);
    gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_MIN_FILTER, gl.LINEAR_MIPMAP_NEAREST);
    gl.generateMipmap(gl.TEXTURE_2D);

    gl.bindTexture(gl.TEXTURE_2D, null);
}

let programInfo;
let buffers;
let rotationMatrix = mat4.create();
let canvasTexture;

export const drawWebGLCanvas = () => {
    const canvas = document.getElementById("webgl-graph");
    const gl = canvas.getContext("webgl");

    gl.clearColor(0.0, 0.0, 0.0, 1.0);

    gl.clear(gl.COLOR_BUFFER_BIT);

    setPositionAttribute(gl, buffers, programInfo);
    gl.useProgram(programInfo.program);

    gl.uniformMatrix4fv(
        programInfo.uniformLocations.rotationMatrix,
        false,
        rotationMatrix,
    );

    fillTexture(gl, canvasTexture);

    gl.activeTexture(gl.TEXTURE0);
    gl.bindTexture(gl.TEXTURE_2D, canvasTexture);
    gl.uniform1i(programInfo.uniformLocations.sampler, 0);

    const offset = 0;
    const vertexCount = 4;
    gl.drawArrays(gl.TRIANGLE_STRIP, offset, vertexCount);
}

export const initWebGLCanvas = () => {
    const canvas = document.getElementById("webgl-graph");
    const gl = canvas.getContext("webgl");

    const shaderProgram = initShaderProgram(gl, vsSource, fsSource);

    programInfo = {
        program: shaderProgram,
        attribLocations: {
            vertexPosition: gl.getAttribLocation(shaderProgram, "vertPos"),
        },
        uniformLocations: {
            rotationMatrix: gl.getUniformLocation(shaderProgram, "rotationMatrix"),
            sampler: gl.getUniformLocation(shaderProgram, "sampler"),
        },
    };
    buffers = initBuffers(gl);
    canvasTexture = initTexture(gl)

    canvas.addEventListener('mousedown', (event) => {
        isMouseDown = true;
    });

    document.addEventListener('mousemove', (event) => {
        if (isMouseDown) {
            angleX += Math.PI * event.movementX / 200;

            angleY += Math.PI * event.movementY / 200;
            angleY = Math.min(Math.PI / 2, angleY);
            angleY = Math.max(-Math.PI / 2, angleY);

            rotationMatrix = mat4.create();
            mat4.rotate(rotationMatrix, rotationMatrix, angleX, vec3.fromValues(0.0, 1.0, 0.0));
            mat4.rotate(rotationMatrix, rotationMatrix, angleY, vec3.fromValues(1.0, 0.0, 0.0));

            drawWebGLCanvas();
        }
    });

    document.addEventListener('mouseup', () => {
        if (isMouseDown) {
            isMouseDown = false;
        }
    });

    canvas.addEventListener('click', async (ev) => {
        const width = canvas.width;
        const height = canvas.height;
        const centerX = width / 2;
        const centerY = height / 2;

        const rect = canvas.getBoundingClientRect();

        const x = (ev.clientX - rect.left - centerX) / width * 2.0;
        const y = (ev.clientY - rect.top - centerY) / height * 2.0;

        const ro = vec3.fromValues(0.0, 0.0, 0.0);
        let rd = vec3.fromValues(0.0, 0.0, 0.0);
        rd = vec3.normalize(rd, vec3.fromValues(x, y, 70.0));

        const p = vec3.fromValues(0.0, 0.0, 90.0);
        const radius = 1.0;

        let rc = vec3.fromValues(0.0, 0.0, 0.0);
        rc = vec3.sub(rc, ro, p);

        const a = vec3.dot(rd, rd);
        const b = 2.0 * vec3.dot(rd, rc);
        const c = vec3.dot(rc, rc) - (radius * radius);
    
        const discr = b * b - 4.0 * a * c;
        if (discr < 0.0) {
            //return -1.0;
        } else {
            const t = (-b - Math.sqrt(discr)) / (2.0 * a);
            let out = vec3.fromValues(0.0, 0.0, 0.0);
            out = vec3.sub(out, vec3.add(out, ro, vec3.scale(out, rd, t)), p);
            
            let hit = vec4.fromValues(out[0], -out[1], out[2], 1.0);
            hit = vec4.transformMat4(hit, hit, rotationMatrix);

            console.log(hit[2]);
            if (hit[2] >= -0.5) {
                return;
            }

            await addPointExternal({ x: hit[0] * 3 / 2, y: hit[1] * 3 / 2 });
        }



        /*
        
        vec2 uv = (-1.0 + 2.0 * gl_FragCoord.xy / resolution.xy) * vec2(aspectRatio, 1.0);

        vec3 ro = vec3(0.0, 0.0, 0.0);
        vec3 rd = normalize(vec3(uv, 70.0));
        vec3 p = vec3(0.0, 0.0, 90.0);
        float radius = 1.0;
        float t = sphere(ro, rd, p, radius);

        float gray = 0.75;

        if (t > 0.0) {
            vec3 hit = (rotationMatrix * vec4(ro + t * rd - p, 1.0)).xyz;
            if (hit.z < 0.0) {
                gl_FragColor = mix(
                    texture2D(sampler, hit.xy / 2.5 - vec2(0.5, 0.5)),
                    vec4(vec3(gray), 1.0),
                    max(0.0, 1.0 - abs(hit.z) * 3.0));

                return;
            }

            gl_FragColor = vec4(vec3(mix(gray, (1.0 - hit.z) / 1.5, hit.z / 2.5)), 1.0);
        } else {
            gl_FragColor = vec4(uv, 1.0, 1.0);
        }

        */
    });
    

    drawWebGLCanvas();
};