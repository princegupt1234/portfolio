"use client";

import { motion } from "framer-motion";
import { TypeAnimation } from "react-type-animation";
import {
  ChevronDown,
  Download,
  Mail,
  ExternalLink,
  Code,
  Terminal,
  Zap,
} from "lucide-react";
import { useEffect, useState } from "react";
import { ResumeModal } from "./ResumeModal";

const Hero = () => {
  const [isResumeModalOpen, setIsResumeModalOpen] = useState(false);
  const [floatingElements, setFloatingElements] = useState<Array<{
    symbol: string;
    x: number;
    y: number;
    duration: number;
    delay: number;
    id: number;
  }>>([]);
  const [mounted, setMounted] = useState(false);

  // Handle hydration
  useEffect(() => {
    setMounted(true);
  }, []);

  // Generate floating elements only on client to avoid hydration mismatch
  useEffect(() => {
    const elements = ["{}", "<>", "()", "[]", "//", "/* */"].map(
      (symbol, index) => ({
        symbol,
        x: Math.random() * 1920,
        y: Math.random() * 1080,
        duration: 10 + Math.random() * 10,
        delay: Math.random() * 5,
        id: index,
      })
    );
    setFloatingElements(elements);
  }, []);

  const scrollToSection = (href: string) => {
    const element = document.querySelector(href);
    if (element) {
      element.scrollIntoView({ behavior: "smooth" });
    }
  };

  const codeSnippet = `const developer = {
  name: "Prince Gupt",
  role: "Full Stack Engineer",
  skills: ["React", "Node.js", "TypeScript"],
  passion: "Building scalable solutions",
  status: "Always learning 🚀"
}`;

  return (
    <>
      <section
        id="home"
        className="min-h-screen flex items-center justify-center relative overflow-hidden transition-colors duration-500 bg-gradient-to-br from-slate-900 via-gray-900 to-zinc-900"
      >
        {/* Background */}
        <div className="absolute inset-0">
          {mounted && (
            <>
              <div
                className="absolute inset-0 transition-colors duration-500 bg-gradient-to-br from-blue-900/20 via-purple-900/20 to-cyan-900/20"
              />

              <div
                className="absolute inset-0"
                style={{
                  backgroundImage: `url("data:image/svg+xml,%3Csvg width='60' height='60' viewBox='0 0 60 60' xmlns='http://www.w3.org/2000/svg'%3E%3Cg fill='none' fillRule='evenodd'%3E%3Cg fill='%2364B5F6' fillOpacity='0.05'%3E%3Ccircle cx='30' cy='30' r='1'/%3E%3C/g%3E%3C/g%3E%3C/svg%3E")`,
                }}
              />
            </>
          )}
        </div>

        {/* Floating Elements */}
        <div
          className="absolute inset-0 overflow-hidden pointer-events-none"
          suppressHydrationWarning
        >
          {floatingElements.map((element) => (
            <motion.div
              key={element.id}
              className="absolute text-2xl font-mono transition-colors duration-500 text-blue-400/20"
              initial={{
                x: element.x,
                y: element.y,
                opacity: 0,
              }}
              animate={{
                y: [null, -100],
                opacity: [0, 0.3, 0],
              }}
              transition={{
                duration: element.duration,
                repeat: Infinity,
                delay: element.delay,
                ease: "linear",
              }}
            >
              {element.symbol}
            </motion.div>
          ))}
        </div>

        {mounted && (
          <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-20 relative z-10">
            <div className="grid lg:grid-cols-2 gap-12 items-center">
              {/* LEFT */}
              <motion.div
                initial={{ opacity: 0, x: -50 }}
                animate={{ opacity: 1, x: 0 }}
                transition={{ duration: 0.8 }}
                className="text-center lg:text-left"
              >
                <div className="inline-flex items-center gap-2 px-4 py-2 rounded-full text-sm font-medium transition-colors duration-500 bg-green-500/10 border border-green-500/20 text-green-400">
                  <div className="w-2 h-2 bg-green-500 rounded-full animate-pulse"></div>
                  Available for new opportunities
                </div>

                <h1 className="text-5xl md:text-7xl font-bold mb-6 leading-tight transition-colors duration-500 text-white">
                  Hi, I'm{" "}
                  <span className="bg-gradient-to-r from-blue-400 via-purple-500 to-cyan-400 bg-clip-text text-transparent">
                    Prince Gupt
                  </span>
                </h1>

                <div className="text-xl md:text-2xl mb-6 h-16 flex items-center justify-center lg:justify-start">
                  <TypeAnimation
                    sequence={[
                      "Full Stack Developer",
                      2000,
                      "React Specialist",
                      2000,
                      "TypeScript Expert",
                      2000,
                      "Problem Solver",
                      2000,
                      "Code Architect",
                      2000,
                    ]}
                    wrapper="span"
                    speed={50}
                    repeat={Infinity}
                    className="bg-gradient-to-r from-cyan-400 to-blue-400 bg-clip-text text-transparent font-semibold"
                  />
                </div>

                <p className="text-lg md:text-xl mb-8 leading-relaxed transition-colors duration-500 text-gray-300">
                  Crafting exceptional digital experiences with modern technologies.
                  Passionate about clean code, scalable architecture, and innovative solutions.
                </p>

                <div className="flex gap-4 flex-wrap justify-center lg:justify-start mb-12">
                  <button
                    onClick={() => scrollToSection("#projects")}
                    className="px-8 py-4 bg-gradient-to-r from-blue-600 to-purple-600 text-white rounded-xl flex items-center gap-3 hover:scale-105 transition-all duration-200 hover:from-blue-700 hover:to-purple-700"
                  >
                    View My Work
                    <ExternalLink className="w-4 h-4" />
                  </button>

                  <button
                    onClick={() => setIsResumeModalOpen(true)}
                    className="px-8 py-4 border-2 rounded-xl flex items-center gap-3 hover:scale-105 transition-all duration-200 border-gray-600 text-gray-300 hover:bg-gray-800"
                  >
                    <Download className="w-5 h-5" />
                    Download Resume
                  </button>

                  <button
                    onClick={() => scrollToSection("#contact")}
                    className="px-8 py-4 rounded-xl flex items-center gap-3 hover:scale-105 transition-all duration-200 bg-gray-800 text-white hover:bg-gray-700"
                  >
                    <Mail className="w-5 h-5" />
                    Let's Connect
                  </button>
                </div>

                <button
                  onClick={() => scrollToSection("#developer-stats")}
                  className="animate-bounce p-3 rounded-full transition-colors duration-500 bg-blue-500/20"
                >
                  <ChevronDown className="w-6 h-6 transition-colors duration-500 text-blue-400" />
                </button>
              </motion.div>

              {/* RIGHT */}
              <motion.div
                initial={{ opacity: 0, x: 50 }}
                animate={{ opacity: 1, x: 0 }}
                transition={{ duration: 0.8 }}
              >
                <div className="rounded-2xl shadow-2xl border overflow-hidden transition-colors duration-500 bg-gray-900 border-gray-700">
                  {/* Code Editor Header */}
                  <div className="px-4 py-3 border-b flex items-center justify-between transition-colors duration-500 bg-gray-800 border-gray-700">
                    <div className="flex items-center gap-2">
                      <div className="flex gap-1">
                        <div className="w-3 h-3 bg-red-500 rounded-full"></div>
                        <div className="w-3 h-3 bg-yellow-500 rounded-full"></div>
                        <div className="w-3 h-3 bg-green-500 rounded-full"></div>
                      </div>
                      <span className="text-sm font-mono transition-colors duration-500 text-gray-400">
                        developer.js
                      </span>
                    </div>
                    <div className="flex items-center gap-2">
                      <Code className="w-4 h-4 transition-colors duration-500 text-gray-400" />
                      <span className="text-xs transition-colors duration-500 text-gray-400">
                        TypeScript
                      </span>
                    </div>
                  </div>

                  {/* Code Content */}
                  <div className="p-6 transition-colors duration-500 bg-gray-900">
                    <div className="flex">
                      {/* Line Numbers */}
                      <div className="text-sm font-mono pr-4 border-r select-none transition-colors duration-500 text-gray-600 border-gray-700">
                        <div className="leading-6">1</div>
                        <div className="leading-6">2</div>
                        <div className="leading-6">3</div>
                        <div className="leading-6">4</div>
                        <div className="leading-6">5</div>
                        <div className="leading-6">6</div>
                        <div className="leading-6">7</div>
                      </div>

                      {/* Code */}
                      <div className="flex-1 font-mono text-sm leading-6 pl-4">
                        <div>
                          <span className="text-purple-400">const</span>{" "}
                          <span className="text-blue-400">developer</span>{" "}
                          <span className="text-gray-300">=</span>{" "}
                          <span className="text-gray-300">{"{"}</span>
                        </div>
                        <div className="pl-4">
                          <span className="text-green-400">name</span>
                          <span className="text-gray-300">:</span>{" "}
                          <span className="text-yellow-300">"Prince Gupt"</span>
                          <span className="text-gray-300">,</span>
                        </div>
                        <div className="pl-4">
                          <span className="text-green-400">role</span>
                          <span className="text-gray-300">:</span>{" "}
                          <span className="text-yellow-300">"Full Stack Engineer"</span>
                          <span className="text-gray-300">,</span>
                        </div>
                        <div className="pl-4">
                          <span className="text-green-400">skills</span>
                          <span className="text-gray-300">:</span>{" "}
                          <span className="text-gray-300">[</span>
                          <span className="text-yellow-300">"React"</span>
                          <span className="text-gray-300">,</span>{" "}
                          <span className="text-yellow-300">"Node.js"</span>
                          <span className="text-gray-300">,</span>{" "}
                          <span className="text-yellow-300">"TypeScript"</span>
                          <span className="text-gray-300">],</span>
                        </div>
                        <div className="pl-4">
                          <span className="text-green-400">passion</span>
                          <span className="text-gray-300">:</span>{" "}
                          <span className="text-yellow-300">"Building scalable solutions"</span>
                          <span className="text-gray-300">,</span>
                        </div>
                        <div className="pl-4">
                          <span className="text-green-400">status</span>
                          <span className="text-gray-300">:</span>{" "}
                          <span className="text-yellow-300">"Always learning 🚀"</span>
                        </div>
                        <div>
                          <span className="text-gray-300">{"}"};</span>
                        </div>
                      </div>
                    </div>

                    {/* Status Bar */}
                    <div className="flex items-center justify-between mt-6 pt-4 border-t transition-colors duration-500 border-gray-700">
                      <div className="flex items-center gap-2 text-green-400">
                        <Terminal className="w-4 h-4" />
                        <span className="text-sm">Ready to innovate ✨</span>
                      </div>
                      <div className="text-xs transition-colors duration-500 text-gray-500">
                        Ln 7, Col 1
                      </div>
                    </div>
                  </div>
                </div>
              </motion.div>
            </div>
          </div>
        )}
      </section>

      {/* Modal */}
      <ResumeModal
        isOpen={isResumeModalOpen}
        onClose={() => setIsResumeModalOpen(false)}
      />
    </>
  );
};

export default Hero;