
/* Copyright (c) 2019-2020 Angourisoft
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class TestGenerator {
    static String tab = "    ";
    static String tab2 = tab + tab;
    static String tab3 = tab2 + tab;
    static String tab4 = tab3 + tab;

    static final String polyTestsPath = "./../Algebra/SolveTest/SolverNumericalTests.cs";
    static final String tableTrigTestsPath = "./../Core/TableTrigConstTest.cs";

    public static String generatePolynomial(String className, int iterCount, int power, boolean complex)
    {
        var sb = new StringBuilder();
        sb.append("namespace UnitTests.Algebra.PolynomialSolverTests\n");
        sb.append("{\n");
        sb.append(tab); sb.append("[TestClass]\n");
        sb.append(tab); sb.append("public class "); sb.append(className); sb.append("\n");
        sb.append(tab); sb.append("{\n");
        sb.append(tab2); sb.append("public static VariableEntity x = \"x\";\n\n\n");
        var rand = new Random(44);
        for(var i = 0; i < iterCount; i++)
        {
            sb.append(tab2); sb.append("[TestMethod]\n");
            sb.append(tab2); sb.append("public void TestAll");
            sb.append("complexNumeric"); sb.append(i + 1); sb.append("_"); sb.append(power); sb.append("()\n");
            sb.append(tab2); sb.append("{\n");
            sb.append(tab3); sb.append("var expr = ");
            for(int j = 0; j < power; j++)
            {
                sb.append("(x - "); sb.append(rand.nextInt(10));
                if (complex) {
                    sb.append(" + MathS.i * ");
                    sb.append(rand.nextInt(10));
                }
                sb.append(")");
                if (j != power - 1)
                    sb.append(" * ");
            }
            sb.append(";\n");
            sb.append(tab3); sb.append("var newexpr = expr.Expand();\n");
            sb.append(tab3); sb.append("foreach (var root in newexpr.SolveEquation(x).FiniteSet())\n");
            sb.append(tab4); sb.append("SolveOneEquation.AssertRoots(newexpr, x, root);\n");
            sb.append(tab2); sb.append("}\n");
        }
        sb.append(tab); sb.append("}\n");
        sb.append("}");
        return sb.toString();
    }

    public static void generatePolynomialTests() throws IOException
    {
        var sb = new StringBuilder();
        sb.append("/*\n");
        sb.append(" * This file was auto-generated by TestGenerator.jar\n");
        sb.append(" * Do not modify it; modify TestGenerator.java and rerun it instead.\n");
        sb.append(" */\n\n\n");
        sb.append("using AngouriMath;\n");
        sb.append("using Microsoft.VisualStudio.TestTools.UnitTesting;\n\n");
        sb.append(generatePolynomial("ClassRealCardanoNumericRoots", 20, 3, false));
        sb.append("\n\n");
        sb.append(generatePolynomial("ClassComplexCardanoNumericRoots", 30, 3, true));
        sb.append("\n\n");
        sb.append(generatePolynomial("ClassRealFerrariNumericRoots", 12, 4, false));
        sb.append("\n\n");
        sb.append(generatePolynomial("ClassComplexFerrariNumericRoots", 8, 4, true));
        var writer = new FileWriter(polyTestsPath);
        writer.write(sb.toString());
        writer.close();
    }

    public static String BuildTests(String funcName)
    {
        return BuildTests(funcName, Arrays.asList());
    }

    public static String BuildTests(String funcName, List<Integer> toIgnore)
    {
        var sb = new StringBuilder();

        sb.append(tab); sb.append("[TestClass]\n");
        sb.append(tab); sb.append("public class TestTrigTableConst"); sb.append(funcName); sb.append("\n");
        sb.append(tab); sb.append("{\n");

        for (var i = 1; i < 30; i++)
        {
            if (toIgnore.contains(i))
                continue;
            sb.append(tab2); sb.append("[TestMethod]\n");
            sb.append(tab2); sb.append("public void "); sb.append(funcName); sb.append(i); sb.append("Test()\n");
            sb.append(tab2); sb.append("{\n");
            sb.append(tab3); sb.append("MathS.Settings.PrecisionErrorCommon.Set(1e-8m);\n");
            sb.append(tab3); sb.append("var toSimplify = MathS."); sb.append(funcName);
            sb.append("(2 * MathS.pi / "); sb.append(i); sb.append(");\n");
            sb.append(tab3); sb.append("var expected = toSimplify.Eval();\n");
            sb.append(tab3); sb.append("var real = toSimplify.Simplify().Eval();\n");
            sb.append(tab3); sb.append("Assert.IsTrue(expected == real, \"expected: \" + expected.ToString() + \"  Got instead: \" + real.ToString());\n");
            sb.append(tab3); sb.append("MathS.Settings.PrecisionErrorCommon.Unset();\n");
            sb.append(tab2); sb.append("}\n\n");
        }

        sb.append(tab); sb.append("}\n");
        return sb.toString();
    }

    public static void generateTrigTableTests() throws IOException
    {
        var sb = new StringBuilder();
        sb.append("/*\n");
        sb.append(" * This file was auto-generated by TestGenerator\n");
        sb.append(" * Do not modify it; modify TestGenerator.java and rerun it instead.\n");
        sb.append(" */\n\n");
        sb.append("/*\n");
        sb.append(" * It's super important to test all following cases because they test replacements for Trigonometric functions\n");
        sb.append(" * so if one is wrong your result might be wrong at all\n");
        sb.append(" */\n\n\n");
        sb.append("using AngouriMath;\n");
        sb.append("using Microsoft.VisualStudio.TestTools.UnitTesting;\n\n");
        sb.append("namespace UnitTests.Core.TrigTableConstTest\n");
        sb.append("{\n");
        // we exclude test #9 because its simplified expression is ambiguous due to cubic roots
        sb.append(BuildTests("Sin", Arrays.asList(9)));
        sb.append("\n");
        sb.append(BuildTests("Cos"));
        sb.append("\n");
        sb.append(BuildTests("Tan", Arrays.asList(4)));
        sb.append("\n");
        sb.append(BuildTests("Cotan", Arrays.asList(1, 2, 4)));
        sb.append("}\n");
        var writer = new FileWriter(tableTrigTestsPath);
        writer.write(sb.toString());
        writer.close();
    }

    public static void main(String []args) throws IOException {
        // generatePolynomialTests();
        generateTrigTableTests();

    }
}
